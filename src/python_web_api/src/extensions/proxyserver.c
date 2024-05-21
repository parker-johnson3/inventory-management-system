#include <Python.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <time.h>
#include <netdb.h>

#define MAX_PORT_LEN 5
#define DEFAULT_PORT 45000
#define DEFAULT_HOST "server"
#define CHUNK_SIZE 512

int ps_err;

/**
 * Connects to the given hostname and port, and returns the file descriptor of
 * the connected socket
 *
 * @param hostname The hostname of the server
 * @param port     The port number of the server
 *
 * @return  The file descriptor of a connected socket on success,
 *          -1 on failure
 */
static int _ps_connect(char* hostname, char* port) {
    int socketfd;  // File descriptor of the (bound) socket
    int gai_err;   // Result of getaddrinfo

    struct addrinfo hints;    // Config for getaddrinfo
    struct addrinfo *result;  // Array of results returned by getaddrinfo
    struct addrinfo *rp;      // Iteration variable

    // Clear stale memory, if any
    memset(&hints, 0, sizeof(hints));

    hints.ai_family = AF_INET;       // Allow IPv4 or IPv6
    hints.ai_socktype = SOCK_STREAM; // TCP socket
    hints.ai_flags = 0;              // Default flags
    hints.ai_protocol = 0;           // Any protocol

    // Get info about the hostname and port number
    if ((gai_err = getaddrinfo(hostname, port, &hints, &result)) != 0) {
        ps_err = gai_err;
        return -1;
    }

    // getaddrinfo() returns a list of address structures.
    // Try each address until we successfully connect(2).
    // If socket(2) (or connect(2)) fails, we (close the socket
    // and) try the next address.
    for (rp = result; rp != NULL; rp = rp->ai_next) {
        socketfd = socket(rp->ai_family, rp->ai_socktype, rp->ai_protocol);

        if (socketfd == -1)
           continue;

        if (connect(socketfd, rp->ai_addr, rp->ai_addrlen) != -1)
           break; /* Success */

        /* Failure */
        close(socketfd);
        socketfd = -1;  // Set to "unbound state"
    }

    return socketfd;
}

/**
 * proxyserver.connect(hostname: str, port: int | str)
 *
 * Connects to the proxyserver at the specified hostname and port
 * Interface to python
 *
 * Returns: The file descriptor the bound socket as a Python int
 */
static PyObject* ps_connect(PyObject* self, PyObject* args, PyObject* kwargs) {
    char* hostname = NULL;  // Hostname of the server, we only accept str
    PyObject* arg_port;     // Port can be str or int, so accept an object
    char port[MAX_PORT_LEN + 1] = {0};  // Param port object converted to str

    // Flexibility for parameter passing by allowing kwargs
    static char* kwlist[] = {"hostname", "port", NULL};

    // Parse passed python args
    if (!PyArg_ParseTupleAndKeywords(args, kwargs, "sO", kwlist,
        &hostname, &arg_port)) {
        // Raise a ValueError if arguments are not specified properly
        PyErr_SetString(PyExc_ValueError,
                        "hostname and port are required to connect!");
        return NULL;
    }

    // Ensure hostname was specified
    if (hostname == NULL) {
        // Raise a ValueError if it wasn't
        PyErr_SetString(PyExc_ValueError,
                        "hostname is required to connect!");
        return NULL;
    }

    // Ensure port number was specified
    if (arg_port == NULL) {
        // Riase a ValueError if it wasn't
        PyErr_SetString(PyExc_ValueError,
                        "port is required to connect!");
        return NULL;
    }

    // Type check the port passed in

    // Check if port was passed as an int
    if (PyLong_Check(arg_port)) {
        // Convert Python int object to a C int
        int _port = PyLong_AsLong(arg_port);

        // Validate port number
        if (_port <= 0 || _port >= 65536) {
            PyErr_Format(PyExc_ValueError, "%i is not a valid port!", _port);
            return NULL;
        }

        // Port number is valid, convert to C string
        snprintf(port, sizeof(port), "%i", _port);
    }
    // Check if port was passed as a str
    else if (PyUnicode_Check(arg_port)) {
        // Convert a Python Unicode Sequence to C String
        PyObject* bytes = PyUnicode_AsUTF8String(arg_port);
        char* str = PyBytes_AsString(bytes);

        Py_XDECREF(bytes); // PyUnicode_AsUTF8String increases the refcount

        int port_len = strlen(str);
        int _port = strtol(str, NULL, 10);

        // Validate port number
        if ((port_len > MAX_PORT_LEN) || _port <= 0 || _port >= 65536) {
            PyErr_Format(PyExc_ValueError, "%s is not a valid port!", str);
            return NULL;
        }

        // Port number is valid
        strcpy(port, str);
    }

    // All args are good now!
    int socketfd;
    if ((socketfd = _ps_connect(hostname, port)) < 0) {
        PyErr_Format(PyExc_OSError, "Failed to connect to %s on port %s!\n%s",
                     hostname, port, gai_strerror(ps_err));
        return NULL; // If _ps_connect fails, raise exception
    }

    // If _ps_connect succeeds, convert socketfd to Python int
    PyObject* res = PyLong_FromLong(socketfd);

    // Return converted Python int
    return res;
}

/**
 * proxyserver.close(hostname: str, port: int | str)
 *
 * Closes the connection to the server
 * Interface to python
 */
static PyObject* ps_close(PyObject* self, PyObject* args, PyObject* kwargs) {
    int socketfd = -1;  // file descriptor of the socket

    // Flexibility for parameter passing by allowing kwargs
    static char* kwlist[] = {"fd", NULL};

    // Parse passed python args
    if (!PyArg_ParseTupleAndKeywords(args, kwargs, "i", kwlist, &socketfd)) {
        // Raise a ValueError if arguments are not specified properly
        PyErr_SetString(PyExc_ValueError,
                        "Must specify the file descriptor (only) to close socket!");
        return NULL;
    }

    // Validate socket file descriptor
    if (socketfd < 0) {
        PyErr_Format(PyExc_ValueError, "Cannot close file descriptor %i",
                     socketfd);
        return NULL;
    }

    // Close the connection. If an error occurs, propagate it.=
    if (close(socketfd) < 0) {
        PyErr_SetFromErrno(PyExc_OSError);
        return NULL;
    }

    Py_RETURN_NONE;
}

/**
 * Reads data from a socket into a dynamically allocated buffer.
 *
 * @param sockfd The socket file descriptor from which to read.
 *
 * @return A pointer to the dynamically allocated buffer containing the
 *         read data, or NULL if an error occurred.
 *
 * @note The caller is responsible for freeing the allocated buffer.
 */
static char* read_socket(int sockfd, int* size) {
    char* buffer = NULL;
    *size = -1;
    size_t buffer_size = 0;
    size_t buffer_capacity = 0;

    ssize_t bytes_read;
    char chunk[CHUNK_SIZE];

    // Read data from the socket in chunks
    while ((bytes_read = recv(sockfd, chunk, sizeof(chunk), 0)) > 0) {
        // Resize the buffer if necessary to accommodate the newly read data
        while (buffer_size + bytes_read > buffer_capacity) {
            // Double the buffer capacity
            size_t new_capacity;
            if (buffer_capacity == 0)
                new_capacity = CHUNK_SIZE;
            else
                new_capacity = buffer_capacity + 2 * CHUNK_SIZE;

            // Reallocate the buffer
            char* new_buffer = realloc(buffer, new_capacity);
            if (new_buffer == NULL) {
                free(buffer);  // Fatal error, release the current buffer
                return NULL;
            }
            buffer = new_buffer;
            buffer_capacity = new_capacity;
        }

        // Copy the newly read data to the end of the buffer
        memcpy(buffer + buffer_size, chunk, bytes_read);
        buffer_size += bytes_read;

        if (bytes_read < CHUNK_SIZE)
            break;
    }

    // recv failed!
    if (bytes_read < 0)
        return NULL;

    *size = buffer_size;
    return buffer;
}

/**
 * proxyserver.recv(fd: int) -> str
 *
 * Recieves data from proxyserver
 *
 * Returns: received data as string
 */
static PyObject* ps_recv(PyObject* self, PyObject* args, PyObject* kwargs) {
    int socketfd = -1;  // file descriptor of the socket

    // Flexibility for parameter passing by allowing kwargs
    static char* kwlist[] = {"fd", NULL};

    // Parse passed python args
    if (!PyArg_ParseTupleAndKeywords(args, kwargs, "i", kwlist, &socketfd)) {
        // Raise a ValueError if arguments are not specified properly
        PyErr_SetString(PyExc_ValueError,
                        "Must specify the file descriptor (only) to read from socket!");
        return NULL;
    }

    // Validate socket file descriptor
    if (socketfd < 0) {
        PyErr_Format(PyExc_ValueError, "Cannot recv file descriptor %i",
                     socketfd);
        return NULL;
    }

    // Read data from socket
    char* resp;
    int size;
    if ((resp = read_socket(socketfd, &size)) == NULL) {
        PyErr_SetFromErrno(PyExc_OSError);
        return NULL;
    }

    // Create Python string from C string
    PyObject* pystr = PyUnicode_FromStringAndSize(resp, size);
    free(resp);
    resp = NULL; // No dangling pointers
    return pystr;
}

/**
 * proxyserver.send(fd: int, data: str) -> int
 *
 * Sends data to the proxyserver
 *
 * Returns: number of bytes sent
 */
static PyObject* ps_send(PyObject* self, PyObject* args, PyObject* kwargs) {
    int socketfd = -1;  // file descriptor of the socket
    char* data = NULL;  // data to send over the socker

    // Flexibility for parameter passing by allowing kwargs
    static char* kwlist[] = {"fd", "data", NULL};

    // Parse passed python args
    if (!PyArg_ParseTupleAndKeywords(args, kwargs, "is", kwlist,
            &socketfd, &data)) {
        // Raise a ValueError if arguments are not specified properly
        PyErr_SetString(PyExc_ValueError,
                        "Must specify the file descriptor and data to send!");
        return NULL;
    }

    // Validate socket file descriptor
    if (socketfd < 0) {
        PyErr_Format(PyExc_ValueError, "Cannot send on file descriptor %i",
                     socketfd);
        return NULL;
    }

    // Ensure data exists
    if (data == NULL) {
        PyErr_SetString(PyExc_ValueError, "No data to send");
        return NULL;
    }

    int total_bytes_sent = 0; // Total bytes sent over the socket
    int bytes_sent;           // Bytes sent every iteration
    int len = strlen(data);
    while (len > 0) {
        // send over the socket
        bytes_sent = send(socketfd, data, len, 0);
        if (bytes_sent < 0) {
            // Some error occured while writing
            PyErr_SetFromErrno(PyExc_OSError);
            return NULL;
        }

        len -= bytes_sent;
        data += bytes_sent;
        total_bytes_sent += bytes_sent;
    }

    // Convert C int to Python int
    PyObject* nsent = PyLong_FromLong(total_bytes_sent);
    return nsent;
}

static PyMethodDef proxyserver_methods[] = {
    {
        "connect", (PyCFunction) ps_connect, METH_VARARGS | METH_KEYWORDS,
        "connect(hostname: str, port: str) -> int\n"
        "Connect to a given hostname and port.\n"
        "Returns the connected socket's file descriptor."
    },
    {
        "send", (PyCFunction) ps_send, METH_VARARGS | METH_KEYWORDS,
        "send(fd: int, data: str) -> int\n"
        "Sends data to the socket with the given file descriptor.\n"
        "Returns the number of bytes sent."
    },
    {
        "recv", (PyCFunction) ps_recv, METH_VARARGS | METH_KEYWORDS,
        "recv(fd: int) -> str\n"
        "Recieves data from the socket with the given file descriptor.\n"
        "Returns bytes recieved as a string."
    },
    {
        "close", (PyCFunction) ps_close, METH_VARARGS | METH_KEYWORDS,
        "close(fd: int)\n"
        "Closes the socket with the given file descriptor."
    },
    {NULL, NULL, 0, NULL}  // Sentinel, marks the end of the array
};

static struct PyModuleDef proxyserver = {
    PyModuleDef_HEAD_INIT,
    "proxyserver",
    "Provides functions to communicate over sockets to the proxyserver",
    -1,
    proxyserver_methods
};


PyMODINIT_FUNC PyInit_proxyserver(void) {
    return PyModule_Create(&proxyserver);
}
