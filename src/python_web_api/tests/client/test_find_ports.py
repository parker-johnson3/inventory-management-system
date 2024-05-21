import unittest

from webserver.internals.proxyclient import ProxyClient


class TestProxyClientFindPorts(unittest.TestCase):
    '''Tests the functionality of ProxyClient.find_ports'''

    def test_find_single_int_port(self):
        '''Case: One listener, int port value'''
        port = 8000

        expected = (port,)
        actual = ProxyClient.find_ports(8000, 1)

        self.assertSequenceEqual(expected, actual)

    def test_find_single_str_port(self):
        '''Case: One listener, str port value'''
        port = '8000'

        expected = (int(port),)
        actual = ProxyClient.find_ports(8000, 1)

        self.assertSequenceEqual(expected, actual)

    def test_find_multiple_int_port(self):
        '''Case: Multiple listeners, int port values'''
        port = (8000, 9000, 10000)

        actual = ProxyClient.find_ports(port, 3)

        self.assertSequenceEqual(port, actual)

    def test_find_multiple_str_port(self):
        '''Case: Multiple listeners, str port values'''
        port = ('8000', '9000', '10000')

        actual = ProxyClient.find_ports(port, 2)
        expected = tuple(map(int, port[:2]))

        self.assertSequenceEqual(expected, actual)

    def test_find_multiple_mixed_port(self):
        '''Case: Multiple listeners, port values of mixed types'''
        port = (8000, '9000', '11000', 1000)

        actual = ProxyClient.find_ports(port, 4)
        expected = tuple(map(int, port))

        self.assertSequenceEqual(expected, actual)

    def test_find_using_iterable(self):
        '''Case: Multiple listeners, ports supplied by tuple iterator'''
        port = (8000, '9000', '11000', 1000)

        actual = ProxyClient.find_ports(iter(port), 4)
        expected = tuple(map(int, port))

        self.assertSequenceEqual(expected, actual)

    def test_find_using_custom_iterable(self):
        '''Case: Multiple listeners, ports supplied by custom iterator'''
        expected = []

        def port_gen():
            for port in range(8000, 12000, 800):
                expected.append(port)
                yield port

        actual = ProxyClient.find_ports(port_gen(), 5)

        self.assertSequenceEqual(expected[:5], actual)

    def test_find_bad_type(self):
        '''Case: Invalid port type'''
        port = 25.5
        with self.assertRaises(TypeError):
            ProxyClient.find_ports(port, 1)

        port = list
        with self.assertRaises(TypeError):
            ProxyClient.find_ports(port, 1)

        port = str
        with self.assertRaises(TypeError):
            ProxyClient.find_ports(port, 1)

        port = [str, list]
        with self.assertRaises(TypeError):
            ProxyClient.find_ports(port, 1)
