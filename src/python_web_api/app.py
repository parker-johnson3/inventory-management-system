from flask import Flask, request, Response
from flask_cors import CORS

import argparse
import functools
import json
import os
import random

from webserver.internals import models
from webserver.internals import proxyclient

jsonify = functools.partial(json.dumps, cls=models.ModelEncoder)
randint = functools.partial(random.randint, 3, 10)

app = Flask(__name__)
CORS(app)

handler = proxyclient.ProxyClient()
_setup = False


@app.route('/')
@handler.register
def home():
    return 'Success!'


@app.route('/healthcheck', methods=['GET'])
def healthcheck():
    return Response(status=204)


def _partial(func):
    @functools.wraps(func)
    def inner(*args, **kwargs):
        return func(request, *args, **kwargs)

    return inner


def app_routes(module):
    # Airplane routes
    app.route('/airplane', methods=['GET', 'POST'],
              )(_partial(module.airplane))
    app.route('/airplane/<int:airplane_id>',
              methods=['GET', 'PUT', 'DELETE'], )(_partial(module.airplane_with_id))

    # Airplane to Component routes
    app.route('/airplanecomponent',
              methods=['GET', 'POST'], )(_partial(module.airplanecomponent))
    app.route('/airplanecomponent/<int:airplanecomponent_id>',
              methods=['GET', 'DELETE'], )(_partial(module.airplanecomponent_with_id))

    # Component routes
    app.route('/component', methods=['GET', 'POST'],
              )(_partial(module.component))
    app.route('/component/<int:component_id>',
              methods=['GET', 'PUT', 'DELETE'], )(_partial(module.component_with_id))

    # Customer routes
    app.route('/customer', methods=['GET', 'POST'],
              )(_partial(module.customer))
    app.route('/customer/<int:customer_id>',
              methods=['GET', 'DELETE'], )(_partial(module.customer_with_id))

    # Facility routes
    app.route('/facility', methods=['GET', 'POST'],
              )(_partial(module.facility))
    app.route('/facility/<int:facility_id>',
              methods=['GET', 'PUT', 'DELETE'], )(_partial(module.facility_with_id))

    # Manager routes
    app.route('/manager', methods=['GET', 'POST'],
              )(_partial(module.manager))
    app.route('/manager/<int:manager_id>',
              methods=['GET', 'DELETE'], )(_partial(module.manager_with_id))

    # Supplier routes
    app.route('/supplier', methods=['GET', 'POST'],
              )(_partial(module.supplier))
    app.route('/supplier/<int:supplier_id>',
              methods=['GET', 'DELETE'], )(_partial(module.supplier_with_id))

    # Supplier to Facility routes
    app.route('/supplierfacility',
              methods=['GET', 'POST'], )(_partial(module.supplierfacility))
    app.route('/supplierfacility/<int:supplierfacility_id>',
              methods=['GET', 'DELETE'], )(_partial(module.supplierfacility_with_id))


def setup_routes(args: argparse.Namespace):
    global _setup
    if _setup:
        return
    if args.mock_backend:
        from views import mock
        app_routes(mock)
    else:
        from views import backend
        backend.set_cache_filename('/src/artifacts/cache.json')
        backend.load_cache()
        app_routes(backend)
        if args.use_ports:
            proxy_ports = args.use_ports.split(',')
        else:
            proxy_ports = args.proxy_port

        backend.handler.configure(hostname=args.proxy_hostname,
                                  port=proxy_ports,
                                  num_listeners=args.num_listeners)

    _setup = True


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    host = os.environ.get('FLASK_RUN_HOST', '0.0.0.0')
    port = int(os.environ.get('SERVER_PORT', 5000))
    debug = os.environ.get('FLASK_DEBUG', 'false').lower() in ('true', '1')

    ps_host = os.environ.get('PROXYSERVER_HOST', 'localhost')
    ps_port = os.environ.get('PROXYSERVER_PORT', 8000)

    parser.add_argument('-fh', '--flask-hostname', type=str, default=host)
    parser.add_argument('-fp', '--flask-port', type=int, default=port)
    parser.add_argument('-fd', '--flask-debug',
                        action='store_true', default=debug)

    parser.add_argument('-ph', '--proxy-hostname', type=str, default=ps_host)
    parser.add_argument('-pp', '--proxy-port', type=int, default=ps_port)
    parser.add_argument('-l', '--num-listeners', type=int, default=1)
    parser.add_argument('-P', '--use-ports', type=str, default='')
    parser.add_argument('-m', '--mock-backend',
                        action='store_true', default=False)

    args = parser.parse_args()

    host = args.flask_hostname
    port = args.flask_port
    debug = args.flask_debug

    setup_routes(args)

    app.run(host=host, port=port, debug=debug)
