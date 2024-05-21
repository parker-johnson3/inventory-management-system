package com.cs506.project.utils;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SocketIOTest {

    @Test
    public void testReadFrom() throws IOException {
        // Setup
        Socket socket = mock(Socket.class);
        String expected = "Hello, World!";
        byte[] inputBytes = expected.getBytes();
        InputStream inputStream = new ByteArrayInputStream(inputBytes);
        when(socket.getInputStream()).thenReturn(inputStream);

        // Execute
        byte[] result = SocketIO.readFrom(socket, 1024);

        // Verify
        assertArrayEquals(inputBytes, result);
    }

    @Test
    public void testWriteToBytes() throws IOException {
        // Setup
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);
        byte[] data = "Hello, World!".getBytes();

        // Execute
        SocketIO.writeTo(socket, data, false);

        // Verify
        assertArrayEquals(data, outputStream.toByteArray());
    }

    @Test
    public void testWriteToString() throws IOException {
        // Setup
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);
        String data = "Hello, World!";

        // Execute
        SocketIO.writeTo(socket, data, false);

        // Verify
        assertArrayEquals(data.getBytes(), outputStream.toByteArray());
    }
}
