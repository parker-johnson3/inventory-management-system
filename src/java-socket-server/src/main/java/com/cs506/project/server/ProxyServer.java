package com.cs506.project.server;

import com.cs506.project.configs.ListenerConfig;
import com.cs506.project.configs.ServerConfig;
import com.cs506.project.configs.WorkerConfig;
import com.cs506.project.server.ProxyServerListener;
import com.cs506.project.server.ProxyServerWorker;

import java.net.Socket;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

/**
 * Represents the Proxy Server application.
 *
 * This class manages the configuration, setup, and execution of the Proxy Server.
 * It is essentially the controller for the server, and uses is backed by
 * {@code ProxyServerListener} and {@code ProxyServerWorker} processes.
 *
 * Instances of this class are thread safe
 *
 * @author Mrigank Kumar
 */
public class ProxyServer implements Runnable {
    // Server configuration
    private final ServerConfig config;
    private int[] ports;
    private String name;

    // Server processes
    private ProxyServerListener[] listeners;
    private ProxyServerWorker[] workers;

    // Server Thread groups
    private final ThreadGroup listenerGroup;
    private final ThreadGroup workerGroup;

    // Server threads per process
    private Thread[] listenerThreads;
    private Thread[] workerThreads;

    // Logger
    private PrintStream logger;

    // The Work Queue
    private BlockingQueue<Socket> workQueue;

    // Optional latch for shutdown hooks
    private CountDownLatch shutdownLatch;

    /**
     * Constructs a new ProxyServer with the specified configuration.
     * Initializes the server with an empty work queue.
     *
     * @param config The server configuration settings.
     *
     * @throws IOException if an I/O error occurs during initialization.
     */
    public ProxyServer(ServerConfig config) throws IOException {
        this(config, new LinkedBlockingQueue<>());
    }

    /**
     * Constructs a new ProxyServer with the specified configuration and
     * work queue.
     *
     * @param config The server configuration settings.
     * @param queue  The shared queue for incoming client connections.
     *
     * @throws IOException if an I/O error occurs during initialization.
     */
    public ProxyServer(ServerConfig config, BlockingQueue<Socket> queue)
        throws IOException {
        if(config == null)
            throw new IllegalArgumentException("Configuration cannot be null");

        if(queue == null)
            throw new IllegalArgumentException("Work queue cannot be null");

        this.config = config;
        this.logger = System.out;  // Default logger

        // If a log file was specified, use that instead
        if (config.logFilePath() != null)
            this.logger = new PrintStream(config.logFilePath());

        // Work queue for the listeners and workers
        this.workQueue = queue;

        this.listenerGroup = new ThreadGroup("ProxyServer_Listeners");
        this.workerGroup = new ThreadGroup("ProxyServer_Workers");

        this.configurePorts();
    }

    private void configurePorts() throws IllegalStateException {
        // If a port list was given, parse it from String to in
        if (config.portList() != null) {
            ports = Arrays.stream(config.portList().split(","))
                            .mapToInt(s -> Integer.parseInt(s.trim()))
                            .toArray();

            int diff = ports.length - config.numListeners();
            if (diff != 0) {
                String errMsg;
                if (diff > 0) // ports.length > config.numListeners()
                    errMsg = "Too many";
                else // ports.length < config.numListeners()
                    errMsg = "Too few";

                errMsg += " ports specified. There are " + config.numListeners()
                  + " listeners, but " + ports.length + " ports were specified.";

                throw new IllegalStateException(errMsg);
            }
        } else {
            // Assign ports sequentially
            ports = new int[config.numListeners()];
            for (int i = 0; i < ports.length; i++)
                ports[i] = config.portHint() + i;
        }
    }

    private void createThreads(ProxyServerListener[] listeners) {
        listenerThreads = new Thread[listeners.length];
        for (int i = 0; i < listenerThreads.length; i++)
            listenerThreads[i] = new Thread(listenerGroup,
                                            listeners[i],
                                            "Listener:" + ports[i]);
    }

    private void createThreads(ProxyServerWorker[] workers) {
        workerThreads = new Thread[workers.length];
        for (int i = 0; i < workerThreads.length; i++)
            workerThreads[i] = new Thread(workerGroup,
                                          workers[i],
                                          "Worker:" + (i + 1));
    }

    /**
     * Sets up the listeners automatically based on the server configurations.
     *
     * This method will spawn the number of listeners specified by
     * {@code ServerConfig.numWorkers()}, all of which will have the same
     * configurations except port numbers.
     *
     * For listeners with different configurations, use {@code useListeners}
     *
     * @see {@link useListeners(ProxyServerListener[])}
     *
     * @throws IOException if an I/O error occurs during setup.
     */
    public void setupListeners() throws IOException {
        listeners = new ProxyServerListener[config.numListeners()];

        // Setup and start the listeners
        for (int i = 0; i < listeners.length; i++) {
            ListenerConfig config = new ListenerConfig(ports[i]);
            listeners[i] = new ProxyServerListener(config, workQueue, logger);
            listeners[i].start();
        }
    }

    /**
     * Uses the given listeners instead of the automatically configured ones.
     *
     * @see {@link setupListeners()}
     *
     * @param listeners An array of listener threads to use.
     */
    public void useListeners(ProxyServerListener[] listeners) {
        this.listeners = listeners;
    }

    /**
     * Returns the listeners used by this ProxyServer
     */
    public ProxyServerListener[] getListeners() {
        return listeners;
    }

    /**
     * Sets up the workers automatically with the specified task to execute.
     *
     * This method will spawn the number of workers specified by
     * {@code ServerConfig.numWorkers()}, all of which will perform same task.
     *
     * For workers with different tasks, use {@code useWorkers}
     *
     * @see {@link useWorkers(ProxyServerWorker[])}
     *
     * @param task The task to be executed by worker threads.
     * @throws IOException if an I/O error occurs during setup.
     */
    public void setupWorkers(ProxyServerTask task) throws IOException {
        int numWorkers = config.numWorkers();
        workers = new ProxyServerWorker[numWorkers];

        // Setup the workers
        for (int i = 0; i < numWorkers; i++) {
            WorkerConfig config = new WorkerConfig(this.config.chunkSize(),
                                                   this.config.timeout(),
                                                   this.config.autoAppend());
            workers[i] = new ProxyServerWorker(config, workQueue, logger);
            workers[i].setTask(task);
            workers[i].start();
        }
    }

    /**
     * Uses the given workers instead of the automatically configured ones
     *
     * @see {@link setupWorkers()}
     *
     * @param listeners An array of listener threads to use.
     */
    public void useWorkers(ProxyServerWorker[] workers) {
        this.workers = workers;
    }

    /**
     * Returns the listeners used by this ProxyServer
     */
    public ProxyServerListener[] getWorkers() {
        return listeners;
    }

    /**
     * This is a shortcut method that sets up the Proxy Server's listener and
     * worker processes with the specified task to execute.
     *
     * @see {@code setupListeners()}
     * @see {@code setupWorkers()}
     *
     * @param task The task to be executed by worker threads.
     *
     * @throws IOException if an I/O error occurs during setup.
     */
    public void setup(ProxyServerTask task) throws IOException {
        setupListeners();
        setupWorkers(task);
    }

    /**
     * Executes the main logic of the Proxy Server.
     */
    @Override
    public void run() {
        name = Thread.currentThread().getName();
        createThreads(listeners);
        createThreads(workers);

        // Initialize shutdown latch for graceful termination
        shutdownLatch = new CountDownLatch(listeners.length + workers.length);
        Arrays.stream(listeners)
              .forEach(l -> l.useShutdownLatch(shutdownLatch));
        Arrays.stream(workers)
              .forEach(l -> l.useShutdownLatch(shutdownLatch));

        // Add shutdown hook to wait for thread termination and close logger
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    shutdownLatch.await();
                } catch (InterruptedException e) {
                    logger.println(name + ": " +  e.getMessage());
                }

                logger.close();
            }
        });

        // Combine Listener and Worker threads for brevity
        Stream.concat(Arrays.stream(listenerThreads),
                      Arrays.stream(workerThreads))
              .forEach(t -> t.start());

        logger.println("Server up!\n");
    }

    /**
     * Stops the Proxy Server by terminating all listener and worker threads.
     */
    public void stop() {
        // Stop all listeners
        Arrays.stream(listeners).forEach(l -> {
            try { l.stop();}
            catch (IOException e) { logger.println(name + ": " +  e.getMessage()); }
        });

        // Stop all workers
        Arrays.stream(workers).forEach(w -> {
            try { w.stop(); }
            catch (IOException e) { logger.println(name + ": " +  e.getMessage()); }
        });
    }

    /**
     * Blocks current thread and waits for the Proxy Server to terminate.
     */
    public void waitForTermination() throws InterruptedException {
        for (Thread t: listenerThreads)
            t.join();

        for (Thread t: workerThreads)
            t.join();
    }
}
