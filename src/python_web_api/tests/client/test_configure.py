import unittest
from webserver.internals import proxyclient


class TestProxyClientConfigure(unittest.TestCase):
    '''Tests the functionality of ProxyClient.configure'''

    def setUp(self):
        '''Setup the handler before each test'''
        self.handler = proxyclient.ProxyClient()

    def test_single_int_port_one_listeners(self):
        '''Case: Only one listener and an int port value'''
        host = '192.168.0.12'
        port = 8000

        self.handler.configure(host, port)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(1, self.handler.num_listeners)
        self.assertEqual((port,), self.handler.ports)

    def test_single_str_port_one_listeners(self):
        '''Case: Only one listener and a str port value'''
        host = '192.168.0.12'
        port = '8000'
        expected_ports = (8000,)

        self.handler.configure(host, port)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(1, self.handler.num_listeners)
        self.assertEqual(expected_ports, self.handler.ports)

    def test_multiple_int_ports_one_listeners(self):
        '''Case: Only one listener and multiple int port values'''
        host = '192.168.0.12'
        port = [1003, 2221, 3124]

        self.handler.configure(host, port)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(1, self.handler.num_listeners)
        self.assertSequenceEqual(port[:1], self.handler.ports)

    def test_multiple_str_ports_one_listeners(self):
        '''Case: Only one listener and multiple str port values'''
        host = '192.168.0.12'
        port = ['1003', '2221', '3124']
        expected_ports = (1003,)

        self.handler.configure(host, port)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(1, self.handler.num_listeners)
        self.assertSequenceEqual(expected_ports, self.handler.ports)

    def test_multiple_mixed_ports_one_listeners(self):
        '''Case: Only one listener and multiple port values of mixed types'''
        host = '192.168.0.12'
        port = ['1003', 2221, '3124', '9873', 1231]
        expected_ports = (1003,)

        self.handler.configure(host, port)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(1, self.handler.num_listeners)
        self.assertSequenceEqual(expected_ports, self.handler.ports)

    def test_single_int_port_mutliple_listeners(self):
        '''Case: Multiple listeners and an int port value'''
        host = '192.168.0.12'
        port = 8000
        nlisteners = 4
        expected_ports = [i + port for i in range(nlisteners)]

        self.handler.configure(host, port, nlisteners)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(nlisteners, self.handler.num_listeners)
        self.assertSequenceEqual(expected_ports, self.handler.ports)

    def test_single_str_port_mutliple_listeners(self):
        '''Case: Multiple listeners and an str port value'''
        host = '192.168.0.12'
        port = '8000'
        nlisteners = 4
        expected_ports = [i + int(port) for i in range(nlisteners)]

        self.handler.configure(host, port, nlisteners)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(nlisteners, self.handler.num_listeners)
        self.assertSequenceEqual(expected_ports, self.handler.ports)

    def test_multiple_int_ports_mutliple_listeners(self):
        '''Case: Multiple listeners and multiple int port values'''
        host = '192.168.0.12'
        port = [1003, 2221, 3124]
        nlisteners = 3

        self.handler.configure(host, port, nlisteners)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(nlisteners, self.handler.num_listeners)
        self.assertSequenceEqual(port, self.handler.ports)

    def test_multiple_str_ports_mutliple_listeners(self):
        '''Case: Multiple listeners and multiple int port values'''
        host = '192.168.0.12'
        port = ['1003', '2221', '3124']
        nlisteners = 3
        expected_ports = tuple(map(int, port))

        self.handler.configure(host, port, nlisteners)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(nlisteners, self.handler.num_listeners)
        self.assertSequenceEqual(expected_ports, self.handler.ports)

    def test_multiple_mixed_ports_mutliple_listeners(self):
        '''Case: Multiple listeners and multiple port values of mixed types'''
        host = '192.168.0.12'
        port = ['1003', 2221, '3124', '9873', 1231]
        nlisteners = 4
        expected_ports = tuple(map(int, port[:nlisteners]))

        self.handler.configure(host, port, nlisteners)

        self.assertEqual(host, self.handler.hostname)
        self.assertEqual(nlisteners, self.handler.num_listeners)
        self.assertSequenceEqual(expected_ports, self.handler.ports)
