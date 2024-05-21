package com.cs506.project.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Utility class for reading from and writing to sockets.
 * Provides methods to read bytes write bytes or strings from a socket
 *
 * @author Mrigank Kumar
 */
public class SocketIO {
    /**
     * Reads bytes from a socket's input stream.
     *
     * @param socket    The socket from which to read.
     * @param chunkSize The size of the chunks to read.
     *
     * @return A byte array containing the read data.
     *
     * @throws IOException if an I/O error occurs while reading from the socket.
     */
    public static byte[] readFrom(Socket socket, int chunkSize)
        throws IOException {
        InputStream sockIn = socket.getInputStream();
        byte[] buffer = new byte[chunkSize];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int nRead;
        do {
            // Read into buffer
            if ((nRead = sockIn.read(buffer)) < 0)
                break;

            // Store buffer in byte array stream
            stream.write(buffer, 0, nRead);

            // If we didn't fill up the buffer, it's likely we've read
            // all data
            if (nRead < buffer.length)
                break;

            // Boundary condition, if we read the exactly chunkSize
            // bytes
            if (buffer[buffer.length - 1] == 0)
                break;
        } while(sockIn.available() > 0);

        return stream.toByteArray();
    }

    /**
     * Writes a byte array to a socket's output stream.
     *
     * @param socket     The socket to which to write.
     * @param data       The byte array to write.
     * @param appendNull Whether to append a null byte after writing the data.
     *
     * @throws IOException if an I/O error occurs while writing to the socket.
     */
    public static void writeTo(Socket socket, byte[] data, boolean appendNull)
        throws IOException {
        OutputStream sockOut = socket.getOutputStream();
        sockOut.write(data);

        // Add extra NUL
        if (appendNull)
            sockOut.write(0);
    }

    /**
     * Writes a string to a socket's output stream.
     *
     * @param socket     The socket to which to write.
     * @param data       The string to write.
     * @param appendNull Whether to append a null byte after writing the data.
     *
     * @throws IOException if an I/O error occurs while writing to the socket.
     */
    public static void writeTo(Socket socket, String data, boolean appendNull)
        throws IOException {
        writeTo(socket, data.getBytes(), appendNull);
    }
}
