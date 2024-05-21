package com.cs506.project.server;

import com.cs506.project.configs.WorkerConfig;
import com.cs506.project.server.ProxyServerTask;
import com.cs506.project.utils.SocketIO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Represents a worker thread in the Proxy Server.
 *
 * This class implements the Runnable interface, allowing instances of
 * ProxyServerWorker to be executed concurrently in separate threads.
 *
 * The worker thread retrieves incoming client connections from a shared work
 * queue and processes them according to the assigned task.
 *
 * @author Mrigank Kumar
 */
public class ProxyServerWorker implements Runnable {
    private boolean active;
    private WorkerConfig config;
    private BlockingQueue<Socket> workQueue;
    private PrintStream logger;
    private ProxyServerTask task;
    private Socket client;
    private CountDownLatch shutdownLatch;
    private String name;

    /**
     * Constructs a new ProxyServerWorker with the specified configuration and
     * work queue.
     * The default log output stream is set to System.out.
     *
     * @param config    The configuration settings for the worker.
     * @param workQueue The shared queue for incoming client connections.
     */
    public ProxyServerWorker(WorkerConfig config,
                             BlockingQueue<Socket> workQueue) {
        this(config, workQueue, System.out);
    }

    /**
     * Constructs a new ProxyServerWorker with the specified configuration, work queue,
     * and log output stream.
     *
     * @param config    The configuration settings for the worker.
     * @param workQueue The shared queue for incoming client connections.
     * @param logger   The output stream for logging worker activity.
     */
    public ProxyServerWorker(WorkerConfig config,
                             BlockingQueue<Socket> workQueue,
                             PrintStream logger) {
        this.config = config;
        this.workQueue = workQueue;
        this.logger = logger;
        this.shutdownLatch = null;
    }

    /**
     * Indicates whether or not the current worker is active
     * 
     * @return {@code true} if the current worker is active,
*              {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    public void useShutdownLatch(CountDownLatch latch) {
        this.shutdownLatch = latch;
    }

    /**
     * Sets the task to be executed by this worker thread.
     *
     * @param task The task to be executed.
     */
    public void setTask(ProxyServerTask task) {
        this.task = task;
    }

    public void start() {
        this.active = true;

        ProxyServerWorker worker = this;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try { worker.stop(); }
                catch (IOException e) { logger.println(name + ": " +  e.getMessage()); }

                if (worker.shutdownLatch != null)
                    worker.shutdownLatch.countDown();
                active = false;
            }
        });
    }

    /**
     * Executes the worker thread logic.
     * This method is called when the thread is started.
     *
     * @throws IllegalStateException If no task has been set before running the
     *                               worker.
     */
    @Override
    public void run() {
        name = Thread.currentThread().getName();
        if (this.task == null) {
            String errMsg = "Cannot run the worker when no task is set";
            throw new IllegalStateException(errMsg);
        }

        while (active && !Thread.interrupted()) {
            client = null; // Reset client connection
            try {
                client = workQueue.take();
            } catch (InterruptedException e) {
                logger.println(name + ": " +  e.getMessage());
                if (Thread.interrupted())
                    break;

                continue;
            }

            // Apply socket read timeout
            try {
                client.setSoTimeout(config.timeout());
            } catch (SocketException e) {
                logger.println(name + ": " +  e.getMessage());
                continue;
            }

            // Read in chunks as long as there is some data to read
            byte[] request;
            try {
                request = SocketIO.readFrom(client, config.chunkSize());
            } catch (IOException e) {
                logger.println(name + ": " +  e.getMessage());
                continue;
            }

            // Handle the request
            String resp = task.handle(request);

            try {
                SocketIO.writeTo(client, resp, config.autoAppend());

                // Request complete!
                client.close();
            } catch (IOException e) {
                logger.println(name + ": " +  e.getMessage());
            }
        }

        // Stop in case current thread was interrupted
        try {
            stop();
        } catch (IOException e) {
            logger.println(name + ": " +  e.getMessage());
        }
    }

    public void stop() throws IOException {
        if (client != null && !client.isClosed())
            client.close();

        if (active) {
            active = false;
            logger.println(name + " going down!");
        }
    }
}
