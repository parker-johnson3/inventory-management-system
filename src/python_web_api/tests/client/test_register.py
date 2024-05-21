import unittest

from webserver.internals import proxyclient


class TestProxyClientRegister(unittest.TestCase):
    '''Tests the functionality of ProxyClient.register'''

    def setUp(self):
        '''Setup the environment'''
        self.host = '192.168.10.12'
        self.port = (8000, 9000, 10000)
        self.nlisteners = 3
        self.handler = proxyclient.ProxyClient()

    def test_register_decorator(self):
        '''Case: ProxyClient.register as a decorator'''
        @self.handler.register
        def func():
            pass

        self.assertTrue(hasattr(func, 'handler'))
        self.assertEqual(self.handler, func.handler)

    def test_registered_decorated_func(self):
        '''Case: A function registered by a ProxyClient.register decorator'''
        @self.handler.register
        def func():
            func.handler.configure(self.host, self.port)

        self.assertTrue(hasattr(func, 'handler'))
        self.assertEqual(self.handler, func.handler)

        with self.assertRaises(AttributeError):
            _ = self.handler.hostname

        with self.assertRaises(AttributeError):
            _ = self.handler.ports

        func()

        self.assertEqual(self.host, self.handler.hostname)
        self.assertSequenceEqual(self.port[:1], self.handler.ports)

    def test_register_as_function(self):
        '''Case: ProxyClient.register as a function'''
        def func():
            pass

        func = self.handler.register(func)

        self.assertTrue(hasattr(func, 'handler'))
        self.assertEqual(self.handler, func.handler)

    def test_registered_func_as_function(self):
        '''Case: a function registered by a ProxyClient.register function'''
        def func():
            func.handler.configure(self.host, self.port)

        func = self.handler.register(func)

        self.assertTrue(hasattr(func, 'handler'))
        self.assertEqual(self.handler, func.handler)

        with self.assertRaises(AttributeError):
            _ = self.handler.hostname

        with self.assertRaises(AttributeError):
            _ = self.handler.ports

        func()

        self.assertEqual(self.host, self.handler.hostname)
        self.assertSequenceEqual(self.port[:1], self.handler.ports)

    def test_register_func_identity(self):
        '''Case: ProxyClient.register doesn't change the underlying function'''

        def func():
            pass

        registered = self.handler.register(func)

        self.assertEqual(func, registered)
