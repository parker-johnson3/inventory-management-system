package com.cs506.project.server;

import com.cs506.project.configs.WorkerConfig;
import com.cs506.project.utils.SocketIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import org.mockito.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class ProxyServerWorkerTest {

    // @Mock
    private WorkerConfig config = new WorkerConfig(1024, 1000, false);

    @Mock
    private BlockingQueue<Socket> workQueue;

    @Mock
    private PrintStream logger;

    @Mock
    private ProxyServerTask task;

    @Mock
    private Socket client;

    private static MockedStatic<SocketIO> io = Mockito.mockStatic(SocketIO.class);

    private ProxyServerWorker worker;

    @BeforeAll
    public static void setUpClass() throws IOException {
        try {
            io.when(() -> SocketIO.readFrom(any(), anyInt()))
            .thenReturn(new byte[10]);
            io.when(() -> SocketIO.writeTo(any(), any(String.class), anyBoolean()))
            .thenAnswer(Answers.RETURNS_DEFAULTS);
            io.when(() -> SocketIO.writeTo(any(), any(byte[].class), anyBoolean()))
            .thenAnswer(Answers.RETURNS_DEFAULTS);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterAll
    public static void tearDownClass() {
        io.close();
    }

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        try {
            when(workQueue.take()).thenReturn(client);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        worker = new ProxyServerWorker(config, workQueue, logger);
        worker.setTask(task);
        worker.start();
    }

    @Test
    public void testStart() {
        assertTrue(worker.isActive());
    }

    @Test
    public void testRun() throws IOException {
        // Start the worker in a new thread
        new Thread(worker).start();

        // Verify that a client was retrieved from the queue
        try {
            verify(workQueue, timeout(1000)).take();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        // Verify that the socket timeout was set
        verify(client, timeout(1000)).setSoTimeout(anyInt());
    }

    @Test
    public void testStop() throws IOException {
        // Start the worker in a new thread
        new Thread(worker).start();
        worker.stop();

        assertFalse(worker.isActive());
    }
}
