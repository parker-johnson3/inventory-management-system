package com.cs506.project.server;

import com.cs506.project.configs.ListenerConfig;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.Formatter;


/**
 * Represents a listener in the Proxy Server.
 *
 * This class implements the Runnable interface, allowing instances of
 * ProxyServerListener to be executed concurrently in separate threads.
 *
 * The listener listens for incoming client connections and enqueues them
 * into a shared work queue for processing by worker threads.
 *
 * @author Mrigank Kumar
 */
public class ProxyServerListener implements Runnable {
    private final ListenerConfig config;
    private final BlockingQueue<Socket> workQueue;
    private final ServerSocket serverSocket;
    private final PrintStream logger;
    private final Formatter formatter;
    private CountDownLatch shutdownLatch;
    private String name;

    private final static String timeFormat;

    static {
        // Formats date/time as "yy/mm/dd hh:mm:ss"
        timeFormat = "%1$ty/%1$tm/%1$td %1$tH:%1$tM:%1$tS";
    }

    /**
     * Constructs a new ProxyServerListener with the specified configuration and
     * work queue.
     * The default log output stream is set to System.out.
     *
     * @param config    The configuration settings for the listener.
     * @param workQueue The shared queue for incoming client connections.
     * @throws IOException if an I/O error occurs when creating the ServerSocket.
     */
    public ProxyServerListener(final ListenerConfig config,
                               final BlockingQueue<Socket> workQueue)
        throws IOException {
        this(config, workQueue, System.out);
    }

    /**
     * Constructs a new ProxyServerListener with the specified configuration,
     * work queue, and log output stream.
     *
     * @param config    The configuration settings for the listener.
     * @param workQueue The shared queue for incoming client connections.
     * @param logger   The output stream for logging listener activity.
     * @throws IOException if an I/O error occurs when creating the ServerSocket.
     */
    public ProxyServerListener(final ListenerConfig config,
                               final BlockingQueue<Socket> workQueue,
                               final PrintStream logger)
        throws IOException {
        this.config = config;
        this.workQueue = workQueue;
        this.logger = logger;
        this.serverSocket = new ServerSocket();
        this.formatter = new Formatter(logger);
        this.shutdownLatch = null;
    }

    public void useShutdownLatch(CountDownLatch latch) {
        this.shutdownLatch = latch;
    }

    /**
     * Starts the listener by binding it to the specified port.
     *
     * @throws IOException if an I/O error occurs when binding the ServerSocket.
     */
    public void start() throws IOException {
        this.serverSocket.bind(new InetSocketAddress(config.port()));
        logger.println("Bound to port " + config.port());

        ProxyServerListener listener = this;

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    listener.stop();
                } catch (IOException e) {
                    logger.println("Failed to shutdown "
                        + Thread.currentThread().getName());
                } finally {
                    if (shutdownLatch != null)
                        shutdownLatch.countDown();
                }
            }
        });
    }

    /**
     * Executes the listener thread logic.
     * This method is called when the thread is started.
     */
    @Override
    public void run() {
        name = Thread.currentThread().getName();
        while (serverSocket.isBound() && !serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();

                // Place the client in the queue FIRST to reduce processing time
                workQueue.put(client);

                // Log the connection
                synchronized (logger) {
                    logger.print(name + " ");
                    formatter.format(timeFormat, System.currentTimeMillis());
                    logger.print(" from " + client.getInetAddress());
                    logger.println(":" + client.getPort());
                }

            } catch (SocketException e) {}
            catch (IOException | InterruptedException e) {
                logger.println(name + ": " +  e.getMessage());
                break;
            }
        }

        // If thread was interrupted
        try {
            this.stop();
        } catch (IOException e) {
            logger.println(name + ": " +  e.getMessage());
        }
    }

    /**
     * Stops the listener by closing the ServerSocket.
     *
     * @throws IOException if an I/O error occurs when closing the ServerSocket.
     */
    public void stop() throws IOException {
        if (serverSocket.isClosed())
            return;

        this.serverSocket.close();
        logger.println(name + " going down!");
    }
}
