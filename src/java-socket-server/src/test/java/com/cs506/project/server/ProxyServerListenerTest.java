package com.cs506.project.server;

import com.cs506.project.configs.ListenerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;

public class ProxyServerListenerTest {

    // @Mock doesn't work on records
    private ListenerConfig config;
    private static int port = 22563;

    @Mock
    private BlockingQueue<Socket> workQueue;

    @Mock
    private PrintStream logger;

    private ProxyServerListener listener;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        config = new ListenerConfig(++port);
        listener = new ProxyServerListener(config, workQueue, logger);
        listener.start();
    }

    @AfterEach
    public void tearDown() throws IOException {
        listener.stop();
    }

    @Test
    public void testStart() throws IOException {
        verify(logger, atLeast(1)).println("Bound to port " + config.port());
    }

    @Test
    public void testStop() throws IOException {
        listener.stop();
        verify(logger, atLeast(1)).println(anyString() + " going down!");
    }

    @Test
    public void testServerSocketIsUp() throws IOException {
        // Start the listener in a new thread
        new Thread(listener).start();

        // Wait for listener to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        try {
            // Try to connect to the server
            Socket clientSocket = new Socket("127.0.0.1", config.port());

            // Check if the connection was successful
            assertTrue(clientSocket.isConnected());
            clientSocket.close();
        } catch (IOException e) {
            fail("Failed to connect to the server", e);
        }

        listener.stop();
    }

    @Test
    public void testBlockingQueueReceivesClientSockets()
        throws IOException, InterruptedException {
        // Start the listener in a new thread
        new Thread(listener).start();

        // Wait for listener to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        final var obj = new Object() {
            Socket clientSocket;
        };

        new Thread(() -> {
            try {
                // Try to connect to the server
                obj.clientSocket = new Socket("127.0.0.1", config.port());

                // Check if the connection was successful
                assertTrue(obj.clientSocket.isConnected());

                obj.clientSocket.close();
            } catch (IOException e) {
                fail("Failed to connect to the server", e);
            }
        }).start(); // Start a new thread that connects to the server

        // Wait for the client to start and establish the connection
        Thread.sleep(1000);

        // Verify that a socket was added to the queue
        verify(workQueue).put(any());
    }
}
