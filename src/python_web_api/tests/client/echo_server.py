import os
import signal
import socket
import sys
import threading
import time
from concurrent.futures import ThreadPoolExecutor


def kill_server_on_test_finish(ppid):
    pid = os.getpid()

    def f():
        while True:
            try:
                os.kill(ppid, 0)
            except OSError:
                os.kill(pid, signal.SIGTERM)
            time.sleep(1)

    thread = threading.Thread(target=f, daemon=True)
    thread.start()


class EchoServer:
    def __init__(self):
        self.n_hits = 0
        self.data = None

    def __call__(self, port):
        BUFSIZE = 1024
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind(('localhost', port))
            s.listen()
            print(f'Server up on {port}')

            while True:
                conn, addr = s.accept()
                with conn:
                    sys.stdout.flush()
                    self.data = ''
                    while True:
                        sys.stdout.flush()

                        data = conn.recv(BUFSIZE)

                        if not data:
                            break

                        conn.sendall(data)
                        self.data += str(data)
                        if len(data) < BUFSIZE:
                            break


def serve(port):
    EchoServer()(port)


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('Usage: python3 echo_server.py <ports>')
        exit()

    ports = tuple(map(int, sys.argv[1:]))

    with ThreadPoolExecutor() as executor:
        for _ in executor.map(serve, ports):
            pass
