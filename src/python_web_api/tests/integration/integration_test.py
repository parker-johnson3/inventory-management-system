import itertools
import random
import socket
import string
import time
import unittest

from concurrent.futures import ThreadPoolExecutor

from webserver.internals import proxyclient


def random_large_string():
    '''Generate a long random string'''
    size = random.randint(1_000, 2_000)
    return ''.join(random.choice(string.printable) for _ in range(size))


class TestProxyClientGet(unittest.TestCase):
    '''Tests the functionality of ProxyClient.get'''
    host = 'proxy_server'
    ports = (18000, 18001, 18002, 18003)

    @classmethod
    def setUpClass(cls):
        '''Tests if the mock servers are up and running'''

        # Check servers are up on all required ports
        for port in TestProxyClientGet.ports:
            connected = False
            # Retry 5 times
            for i in range(1, 6):
                try:
                    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                        s.connect((cls.host, port))

                    # If no error happened, we connected successfully
                    connected = True
                    break
                except Exception:
                    # Wait 1 second before retrying
                    print(f'\nRetrying to reach localhost:{port} ({i}/5)')
                    time.sleep(1)

            # This will result in a test error as the setup failed!
            assert connected, f'Failed to connect to localhost:{port}'

    def setUp(self):
        self.handler = proxyclient.ProxyClient()

    def test_get_single_server(self):
        '''Tests the ProxyClient with one listener server'''
        host = TestProxyClientGet.host
        ports = TestProxyClientGet.ports

        # Send a request to every server once, then reconfigure
        for port in ports:
            port = TestProxyClientGet.ports[0]
            self.handler.configure(host, port)

            request = f'Testing ProxyClient.get on server at {port = }'
            response = self.handler.get(request)

            # Servers are echo servers
            self.assertEqual(request, response)

    def test_get_two_servers(self):
        '''Tests the ProxyClient.get with two listener servers'''
        host = TestProxyClientGet.host
        ports = TestProxyClientGet.ports

        # Test all combinations of 2 servers
        for pair in itertools.combinations(ports, 2):
            self.handler.configure(host, pair, num_listeners=2)

            for port in pair:
                request = f'Testing ProxyClient.get on server at {port = }'
                response = self.handler.get(request)

                # Servers are echo servers
                self.assertEqual(request, response)

    def test_get_almost_all_servers(self):
        '''Tests the ProxyClient.get with multiple listener servers'''
        host = TestProxyClientGet.host
        ports = TestProxyClientGet.ports

        n = len(ports) - 1

        # Test all combinations of `n - 1` servers
        for combo in itertools.combinations(ports, n):
            self.handler.configure(host, combo, num_listeners=n)

            for port in combo:
                request = f'Testing ProxyClient.get on server at {port = }'
                response = self.handler.get(request)

                # Servers are echo servers
                self.assertEqual(request, response)

    def test_get_all_servers(self):
        '''Tests the ProxyClient.get with multiple listener servers'''
        host = TestProxyClientGet.host
        ports = TestProxyClientGet.ports

        self.handler.configure(host, ports, num_listeners=len(ports))

        # Test all servers
        for port in ports:
            request = f'Testing ProxyClient.get on server at {port = }'
            response = self.handler.get(request)

            # Servers are echo servers
            self.assertEqual(request, response)

    def test_get_large_payload_single_server(self):
        '''Tests ProxyClient.get with a large payload on a single server'''
        host = TestProxyClientGet.host
        ports = TestProxyClientGet.ports

        # Payload is a very large piece of random text
        request = random_large_string()

        self.handler.configure(host, ports[0], num_listeners=1)

        response = self.handler.get(request)

        # Servers are echo servers
        self.assertEqual(request, response)

    def test_get_large_payload_mutliple_servers(self):
        '''Tests ProxyClient.get with a large payload on multiple servers'''
        host = TestProxyClientGet.host
        ports = TestProxyClientGet.ports

        # Payload is a very large piece of random text
        request = random_large_string()

        self.handler.configure(host, ports, num_listeners=len(ports))

        for port in ports:
            response = self.handler.get(request)

            # Servers are echo servers
            self.assertEqual(request, response)

    def test_get_large_payload_mutliple_server(self):
        '''Tests ProxyClient.get with a large payload on multiple servers'''
        host = TestProxyClientGet.host
        ports = TestProxyClientGet.ports

        # Payload is a very large piece of random text
        request = random_large_string()

        self.handler.configure(host, ports, num_listeners=len(ports))

        for port in ports:
            response = self.handler.get(request)

            # Servers are echo servers
            self.assertEqual(request, response)

    def test_get_concurrent_requests(self):
        '''Tests ProxyClient.get with a large payload on multiple servers'''
        host = TestProxyClientGet.host
        ports = TestProxyClientGet.ports

        self.handler.configure(host, ports, num_listeners=len(ports))

        def req(_) -> tuple[str, str]:
            # Payload is a very large piece of random text
            request = random_large_string()
            return (request, self.handler.get(request))

        with ThreadPoolExecutor() as executor:
            for (request, response) in executor.map(req, range(12)):
                self.assertEqual(request, response)


if __name__ == '__main__':
    unittest.main()
