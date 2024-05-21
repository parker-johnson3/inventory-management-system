import threading
from werkzeug.serving import make_server
import logging
import flask
from app import app, setup_routes
from typing import Optional, Any, Mapping
from dataclasses import dataclass

log = logging.getLogger('werkzeug')


@dataclass
class MockNamespace:
    mock_backend: bool = True


class ServerThread(threading.Thread):
    def __init__(self, app: flask.Flask, *args, **kwargs):
        super().__init__()

        host = kwargs.get('host', '0.0.0.0')
        port = int(kwargs.get('port', 5000))
        setup_routes(MockNamespace())  # type:ignore[arg-type]
        self.server = make_server(host, port, app)
        self.ctx = app.app_context()
        self.ctx.push()

    def run(self):
        logging.info('Starting server')
        self.server.serve_forever()

    def shutdown(self):
        logging.info('Stopping server')
        self.server.shutdown()


class Server:
    def __init__(self, *args: tuple[Any], **kwargs: Mapping[Any, Any]):
        self.server: Optional[ServerThread] = None
        self.args = args
        self.kwargs = kwargs

        if 'logLevel' in kwargs:
            log.setLevel(str(kwargs['logLevel']))
        else:
            log.setLevel(logging.ERROR)

    def start(self):
        self.server = ServerThread(app, *self.args, **self.kwargs)
        self.server.start()
        logging.info('Server up!')

    def stop(self):
        if self.server is not None:
            self.server.shutdown()
        logging.info('Server down!')
