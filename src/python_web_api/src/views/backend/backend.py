from typing import Callable, Sequence

import functools
import json
import threading
from flask import Response

from webserver.internals import models
from webserver.internals import proxyclient
from webserver.internals import proxyrequest

jsonify = functools.partial(json.dumps, cls=models.ModelEncoder)
handler = proxyclient.ProxyClient()

_cache_metadata = {
    'cache': dict(),
    'lock': threading.Lock(),
    'filename': None,
}


def set_cache_filename(filename: str):
    _cache_metadata['filename'] = filename


def load_cache():
    global _cache
    try:
        with _cache_metadata['lock']:
            with open(_cache_metadata['filename'], 'r') as f:
                data = json.load(f)
                _cache_metadata['cache'] = {k: json.loads(
                    v, cls=models.ModelDecoder) for k, v in data.items()}
    except FileNotFoundError:
        pass


def save_cache():
    with _cache_metadata['lock']:
        with open(_cache_metadata['filename'], 'w') as f:
            data = {k: json.dumps(v, cls=models.ModelEncoder)
                    for k, v in _cache_metadata['cache'].items()}
            json.dump(data, f, cls=models.ModelEncoder)


def add_encoders(entity_type: type, types: Sequence[str]) -> Callable:
    '''Registers encoders for the given entity and request types

    Args:
        entity_type (type): One of the models.Model subclasses types
        types (Sequence[str]): [description]
    '''
    assert issubclass(entity_type, models.Model), (
        f'{entity_type = } is not the right entity type'
    )

    # Here we store the request encoders
    _map: dict[str, proxyrequest.ProxyRequestEncoder] = {}

    # Validate and convert str values to ProxyRequestType values
    # And add an encoder for that type.
    for type_ in types:
        pr_type = proxyrequest.ProxyRequestType.from_str(type_)
        _map[type_] = proxyrequest.ProxyRequestEncoder(pr_type, entity_type)

    def decorator(func: Callable) -> Callable:
        setattr(func, 'get_encoder', lambda type_: _map[type_])
        return func

    return decorator


def add_decoder(model_name: str) -> Callable:
    '''Registers a decoder for a given view handler'''

    def decorator(func: Callable) -> Callable:
        setattr(func, 'decoder', proxyrequest.ProxyRequestDecoder(model_name))
        return func

    return decorator


def auto_handle_get(with_id: bool = False) -> Callable:
    '''Add a handler for GET requests, as most views have the same operations.

    Args:
        func (Callable): Intercept and handle GET requests for this view
    '''
    def decorator(func: Callable) -> Callable:
        @functools.wraps(func)
        def inner(request, *args, **kwargs):
            # Non GET requests handled by default implementation
            if request.method != 'GET':
                return func(request, *args, **kwargs)

            # If view looks up by id, parse id from params
            if with_id:
                # views have names like "airplane_with_id",
                # so remove the "_with_id"
                kind = func.__name__.replace('_with_id', '')

                if _cache_metadata['cache'].get(kind, None) is None:
                    globals()[kind](request)

                if not _cache_metadata['cache'][kind]:
                    return {'error': 'Caching failed!'}

                # the params are named "airplane_id"
                # so the kind from above + "_id" gives the kwargs key
                ID = kwargs.get(f'{kind}_id', -1)

                with _cache_metadata['lock']:
                    # Get cached data of the given kind
                    data = _cache_metadata['cache'][kind]

                    # get one entity, since we are looking up by specific ID
                    resp = next(filter(lambda x: x.ID == ID, data), None)

                if resp:  # If ID was found, return that record
                    return jsonify(resp)
                return Response(response=f'{kind} with {ID = } not found',
                                status=404)
            else:
                # If cached, return that
                with _cache_metadata['lock']:
                    if _cache_metadata['cache'].get(func.__name__) is not None:
                        return jsonify(_cache_metadata['cache'][func.__name__])

                # Get the function's READ encoder
                encoder = func.get_encoder('READ')

                # Get everything
                req = encoder.encode()

                # Use the function's ProxyClient handler to forward request
                resp = func.handler.get(req)

                # Decode the response using the function's decoder
                try:
                    decoded = func.decoder.decode(resp)
                    data = decoded['data']
                    with _cache_metadata['lock']:
                        if func.__name__ not in _cache_metadata['cache']:
                            _cache_metadata['cache'][func.__name__] = data
                        else:
                            _cache_metadata['cache'].extend(data)

                    threading.Thread(target=save_cache).start()
                    return jsonify(data)
                except Exception:
                    return {'error': 'Failed to retrieve data!'}

        return inner

    return decorator


def auto_handle_delete(func: Callable) -> Callable:
    '''Add a handler for DELETE requests, as most views have the same operations.

    Args:
        func (Callable): Intercept and handle DELETE requests for this view
    '''
    # Safety check, DELETEs only work by ID
    if '_with_id' not in func.__name__:
        return func

    kind = func.__name__.replace('_with_id', '')

    @functools.wraps(func)
    def inner(request, *args, **kwargs):
        # Non DELETE requests handled by default implementation
        if request.method != 'DELETE':
            return func(request, *args, **kwargs)

        # If cache for this kind is not available, load in the cache
        if _cache_metadata['cache'].get(kind, None) is None:
            request.method = 'GET'
            globals()[kind](request)

        # the params are named "airplane_id"
        # so the kind from above + "_id" gives the kwargs key
        ID = kwargs.get(f'{kind}_id', -1)

        with _cache_metadata['lock']:
            # Get cached data of the given kind
            data = _cache_metadata['cache'][kind]

            # get one entity, since we are looking up by specific ID
            resp = next(filter(lambda x: x.ID == ID, data), None)

        if resp:  # If ID was found, return that record
            with _cache_metadata['lock']:
                print(f"{len(_cache_metadata['cache'][kind]) = }")
                _cache_metadata['cache'][kind].remove(resp)
                print(f"{len(_cache_metadata['cache'][kind]) = }")
            threading.Thread(target=save_cache).start()
            return jsonify({'success': True})
        return Response(response=f'{kind} with {ID = } not found',
                        status=404)

    return inner


def auto_handle_post(func: Callable) -> Callable:
    '''Add a handler for POST requests, as most views have the same operations.

    Args:
        func (Callable): Intercept and handle POST requests for this view
    '''
    # Safety check, POSTs work without IDs
    if '_with_id' in func.__name__:
        return func

    kind: str = func.__name__

    @functools.wraps(func)
    def inner(request, *args, **kwargs):
        # Non POST requests handled by default implementation
        if request.method != 'POST':
            return func(request, *args, **kwargs)

        data = request.get_json()
        _type = data.pop('type', kind).lower()

        curr_max = max(_cache_metadata['cache'][_type], key=lambda x: x.ID)

        if curr_max:
            data['ID'] = curr_max.ID + 1
        else:
            data['ID'] = 1

        model = models.decoder_mapping[_type.capitalize()](**data)

        with _cache_metadata['lock']:
            if _type not in _cache_metadata['cache']:
                _cache_metadata['cache'][_type] = []

            _cache_metadata['cache'][_type].append(model)
            threading.Thread(target=save_cache).start()

        return jsonify({'success': True})
    return inner


@auto_handle_post
@auto_handle_get()
@handler.register
@add_decoder('Component')
@add_encoders(models.Component, types=['READ', 'UPDATE'])
def component(request):
    return f'Success on "/component" with method {request.method}'


@auto_handle_delete
@auto_handle_get(with_id=True)
@handler.register
@add_decoder('Component')
@add_encoders(models.Component, types=['READ', 'UPDATE', 'DELETE'])
def component_with_id(request, component_id: int):
    return (
        f'Success on "/component/{{ID}}" with method {request.method}\n'
        f'{component_id = }'
    )


@auto_handle_post
@auto_handle_get()
@handler.register
@add_decoder('Airplane')
@add_encoders(models.Airplane, types=['READ', 'DELETE'])
def airplane(request):
    return f'Success on "/airplane" with method {request.method}'


@auto_handle_delete
@auto_handle_get(with_id=True)
@handler.register
@add_decoder('Airplane')
@add_encoders(models.Airplane, types=['READ', 'UPDATE', 'DELETE'])
def airplane_with_id(request, airplane_id: int):
    return (
        f'Success on "/airplane/{{ID}}" with method {request.method}\n'
        f'{airplane_id = }'
    )


@auto_handle_get()
@handler.register
@add_decoder('AirplaneToComponent')
@add_encoders(models.AirplaneToComponent, types=['READ', 'UPDATE'])
def airplanecomponent(request):
    return f'Success on "/airplanecomponent" with method {request.method}'


@auto_handle_delete
@auto_handle_get(with_id=True)
@handler.register
@add_decoder('AirplaneToComponent')
@add_encoders(models.AirplaneToComponent, types=['READ', 'DELETE'])
def airplanecomponent_with_id(request, airplanecomponent_id: int):
    return (
        f'Success on "/airplanecomponent/{{ID}}" with method {request.method}\n'
        f'{airplanecomponent_id = }'
    )


@auto_handle_post
@auto_handle_get()
@handler.register
@add_decoder('Facility')
@add_encoders(models.Facility, types=['READ', 'UPDATE'])
def facility(request):
    return f'Success on "/facility" with method {request.method}'


@auto_handle_delete
@auto_handle_get(with_id=True)
@handler.register
@add_decoder('Facility')
@add_encoders(models.Facility, types=['READ', 'CREATE', 'DELETE'])
def facility_with_id(request, facility_id: int):
    return (
        f'Success on "/facility/{{ID}}" with method {request.method}\n'
        f'{facility_id = }'
    )


@auto_handle_get()
@handler.register
@add_decoder('Customer')
@add_encoders(models.Customer, types=['READ', 'UPDATE'])
def customer(request):
    return f'Success on "/customer" with method {request.method}'


@auto_handle_get(with_id=True)
@handler.register
@add_decoder('Customer')
@add_encoders(models.Customer, types=['READ', 'DELETE'])
def customer_with_id(request, customer_id: int):
    return (
        f'Success on "/customer/{{ID}}" with method {request.method}\n'
        f'{customer_id = }'
    )


@auto_handle_get()
@handler.register
@add_decoder('Supplier')
@add_encoders(models.Supplier, types=['READ', 'UPDATE'])
def supplier(request):
    return f'Success on "/supplier" with method {request.method}'


@auto_handle_get(with_id=True)
@handler.register
@add_decoder('Supplier')
@add_encoders(models.Supplier, types=['READ', 'DELETE'])
def supplier_with_id(request, supplier_id: int):
    return (
        f'Success on "/supplier/{{ID}}" with method {request.method}\n'
        f'{supplier_id = }'
    )


@auto_handle_get()
@handler.register
@add_decoder('SupplierToFacility')
@add_encoders(models.SupplierToFacility, types=['READ', 'UPDATE'])
def supplierfacility(request):
    return f'Success on "/supplierfacility" with method {request.method}'


@auto_handle_get(with_id=True)
@handler.register
@add_decoder('SupplierToFacility')
@add_encoders(models.SupplierToFacility, types=['READ', 'DELETE'])
def supplierfacility_with_id(request, supplierfacility_id: int):
    return (
        f'Success on "/supplierfacility/{{ID}}" with method {request.method}\n'
        f'{supplierfacility_id = }'
    )


@auto_handle_post
@auto_handle_get()
@handler.register
@add_decoder('Manager')
@add_encoders(models.Manager, types=['READ', 'UPDATE'])
def manager(request):
    return f'Success on "/manager" with method {request.method}'


@auto_handle_delete
@auto_handle_get(with_id=True)
@handler.register
@add_decoder('Manager')
@add_encoders(models.Manager, types=['READ', 'DELETE'])
def manager_with_id(request, manager_id: int):
    return (
        f'Success on "/manager/{{ID}}" with method {request.method}\n'
        f'{manager_id = }'
    )
