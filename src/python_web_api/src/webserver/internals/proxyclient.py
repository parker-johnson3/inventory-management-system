# mypy: disable-error-code="no-redef"

'''
Module for interacting with a proxy server.

This module provides a `ProxyClient` class for sending requests to a proxy server.
'''

from typing import Iterable, Callable
import itertools

import proxyserver as ps


class ProxyClient:
    '''A client for interacting with a proxy server.

    Attributes:
        hostname (str): The hostname of the proxy server.
        num_listeners (int): The number of workers on the server.
        ports (tuple[int | str, ...]): The ports to use for the server.
    '''

    def __init__(self):
        self.__configured = False

    def configure(self, hostname: str, port: int | str | Iterable[int | str],
                  num_listeners: int = 1):
        '''Configures and Initializes the ProxyClient.

        Args:
            hostname (str): The hostname of the proxy server.
            port (int | str | Iterable[int | str]): The port number or list of
                port numbers to use.
            num_listeners (int, optional): The number of Proxy Server listeners.
                Defaults to 1.
        '''
        self.hostname = hostname
        self.num_listeners = num_listeners
        self.ports = ProxyClient.find_ports(port, num_listeners)
        self._port_supplier = itertools.cycle(self.ports)
        self.__configured = True

    def get(self, request: str | bytes) -> str:
        '''Send a request to the proxy server.

        Args:
            request (str | bytes): The request to send.

        Returns:
            str: The response from the server.
        '''
        if not self.__configured:
            raise ConnectionResetError('The ProxyClient is not configured yet')

        fd = ps.connect(self.hostname, next(self._port_supplier))
        if isinstance(request, bytes):
            request: str = request.decode('utf-8')
        ps.send(fd, request)
        resp = ps.recv(fd)
        ps.close(fd)
        if resp[-1] == '\0':
            resp = resp[:-1]
        return resp

    def register(self, func: Callable) -> Callable:
        '''Register the current handler as an attribute on the given callable.

        Args:
            func (Callable): The function to register the current handler on

        Returns:
            Callable: The same callable, which now has been registered
        '''
        if hasattr(func, 'handler'):
            raise ValueError(f'Callable {func} already has the attribute '
                             '"handler".')

        setattr(func, 'handler', self)

        return func

    @staticmethod
    def find_ports(port_hint: int | str | Iterable[int | str],
                   num_listeners: int) -> tuple[int | str, ...]:
        '''Determine the port per Proxy Server listener.

        Args:
            port_hint (int | str | Iterable[int | str]): The port hint or list
                of ports.
            num_listeners (int): The number of Proxy Server listeners.

        Returns:
            tuple[int | str, ...]: A tuple of ports.
        '''
        if isinstance(port_hint, (int, str)):
            port_as_int: int = int(port_hint)
            return tuple((i + port_as_int for i in range(num_listeners)))

        if isinstance(port_hint, Iterable):
            return tuple(map(int, port_hint))[:num_listeners]

        raise TypeError('Port must be specifed as int, str or list of int/str')
