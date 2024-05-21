from typing import Callable
from flask import Response

import random
import functools
import json

from webserver.internals import models
from views.mock import mock_data

jsonify = functools.partial(json.dumps, cls=models.ModelEncoder)
randint = functools.partial(random.randint, 3, 10)

mock_data.deterministic()

__all__ = (
    'component',
    'component_with_id',
    'airplane',
    'airplane_with_id',
    'airplanecomponent',
    'airplanecomponent_with_id',
    'facility',
    'facility_with_id',
    'customer',
    'customer_with_id',
    'supplier',
    'supplier_with_id',
    'supplierfacility',
    'supplierfacility_with_id',
    'manager',
    'manager_with_id',
)


def try_deterministic(func: Callable) -> Callable:
    '''Makes Mock Data deterministic'''

    lookup = 'id' in func.__name__

    kind = func.__name__.replace('_with_id', '')
    assert kind in mock_data.deterministic._static_vars, (
        f'Decorator is not applicable for {func}')

    @functools.wraps(func)
    def inner(request, *args, **kwargs):
        # Only patch get requests
        method = request.method.upper()
        if method not in ['GET', 'DELETE']:
            return func(request, *args, **kwargs)

        # Get deterministic data
        data = mock_data.deterministic(kind=kind)
        deleted = mock_data.deterministic.deleted

        # If this was an ID function, lookup that ID
        if lookup:
            ID = kwargs.get(f'{kind}_id', -1)
            resp = next(filter(lambda x: x.ID == ID, data), None)

            if resp:  # If ID was found, return that record
                if method == 'DELETE':
                    data.remove(resp)
                    deleted.append(resp)
                    return jsonify({'success': True})
                else:
                    return jsonify(resp)
            else:  # Else return random record
                if next(filter(lambda x: x.ID == ID, deleted), None):
                    return Response(response=f'{kind} with {ID = } not found',
                                    status=404)
                return func(request, *args, **kwargs)

        # If this was not an ID function, but still marked deterministic,
        # return deterministic list of records
        return jsonify(data)

    return inner


@try_deterministic
def component(request):
    '''Handles a component request, serves mock data'''
    if request.method == 'GET':
        return jsonify(list(mock_data.component(randint())))
    return f'Success on "/component" with method {request.method}'


@try_deterministic
def component_with_id(request, component_id: int):
    '''Handles a component by id request, serves mock data'''
    if request.method == 'GET':
        data: models.Component = next(mock_data.component())
        data.ID = component_id
        return jsonify(data)
    return (
        f'Success on "/component/{{ID}}" with method {request.method}\n'
        f'{component_id = }'
    )


@try_deterministic
def airplane(request):
    '''Handles a airplane request, serves mock data'''
    if request.method == 'GET':
        return jsonify(list(mock_data.airplane(randint())))
    return f'Success on "/airplane" with method {request.method}'


@try_deterministic
def airplane_with_id(request, airplane_id: int):
    '''Handles a airplane by id request, serves mock data'''
    if request.method == 'GET':
        data: models.Airplane = next(mock_data.airplane())
        data.ID = airplane_id
        return jsonify(data)
    return (
        f'Success on "/airplane/{{ID}}" with method {request.method}\n'
        f'{airplane_id = }'
    )


@try_deterministic
def airplanecomponent(request):
    '''Handles a airplane component request, serves mock data'''
    if request.method == 'GET':
        return jsonify(list(mock_data.airplane_to_component(randint())))
    return f'Success on "/airplanecomponent" with method {request.method}'


@try_deterministic
def airplanecomponent_with_id(request, airplanecomponent_id: int):
    '''Handles a airplane component by id request, serves mock data'''
    if request.method == 'GET':
        data: models.AirplaneToComponent = next(
            mock_data.airplane_to_component())
        data.ID = airplanecomponent_id
        return jsonify(data)
    return (
        f'Success on "/airplanecomponent/{{ID}}" with method {request.method}\n'
        f'{airplanecomponent_id = }'
    )


@try_deterministic
def facility(request):
    '''Handles a facility request, serves mock data'''
    if request.method == 'GET':
        return jsonify(list(mock_data.facility(randint())))
    return f'Success on "/facility" with method {request.method}'


@try_deterministic
def facility_with_id(request, facility_id: int):
    '''Handles a facility by id request, serves mock data'''
    if request.method == 'GET':
        data: models.Facility = next(mock_data.facility())
        data.ID = facility_id
        return jsonify(data)
    return (
        f'Success on "/facility/{{ID}}" with method {request.method}\n'
        f'{facility_id = }'
    )


def customer(request):
    '''Handles a customer request, serves mock data'''
    if request.method == 'GET':
        return jsonify(list(mock_data.customer(randint())))
    return f'Success on "/customer" with method {request.method}'


def customer_with_id(request, customer_id: int):
    '''Handles a customer by id request, serves mock data'''
    if request.method == 'GET':
        data: models.Customer = next(mock_data.customer())
        data.ID = customer_id
        return jsonify(data)
    return (
        f'Success on "/customer/{{ID}}" with method {request.method}\n'
        f'{customer_id = }'
    )


def supplier(request):
    '''Handles a supplier request, serves mock data'''
    if request.method == 'GET':
        return jsonify(list(mock_data.supplier(randint())))
    return f'Success on "/supplier" with method {request.method}'


def supplier_with_id(request, supplier_id: int):
    '''Handles a supplier by id request, serves mock data'''
    if request.method == 'GET':
        data: models.Supplier = next(mock_data.supplier())
        data.ID = supplier_id
        return jsonify(data)
    return (
        f'Success on "/supplier/{{ID}}" with method {request.method}\n'
        f'{supplier_id = }'
    )


def supplierfacility(request):
    '''Handles a supplier facility request, serves mock data'''
    if request.method == 'GET':
        return jsonify(list(mock_data.supplier_to_facility(randint())))
    return f'Success on "/supplierfacility" with method {request.method}'


def supplierfacility_with_id(request, supplierfacility_id: int):
    '''Handles a supplier facility by id request, serves mock data'''
    if request.method == 'GET':
        data: models.SupplierToFacility = next(
            mock_data.supplier_to_facility())
        data.ID = supplierfacility_id
        return jsonify(data)
    return (
        f'Success on "/supplierfacility/{{ID}}" with method {request.method}\n'
        f'{supplierfacility_id = }'
    )


def manager(request):
    '''Handles a manager request, serves mock data'''
    if request.method == 'GET':
        return jsonify(list(mock_data.manager(randint())))
    return f'Success on "/manager" with method {request.method}'


def manager_with_id(request, manager_id: int):
    '''Handles a manager by id request, serves mock data'''
    if request.method == 'GET':
        data: models.Manager = next(mock_data.manager())
        data.ID = manager_id
        return jsonify(data)
    return (
        f'Success on "/manager/{{ID}}" with method {request.method}\n'
        f'{manager_id = }'
    )
