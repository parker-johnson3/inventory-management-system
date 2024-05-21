package com.cs506.project.configs;


/**
 * Represents the configuration settings for the Proxy Server.
 *
 * This record encapsulates various parameters used to configure the behavior of
 * the server, including the number of listeners and workers, port numbers,
 * chunk size, timeout duration, and logging settings.
 *
 * Instances of ServerConfig are immutable and can be used to store
 * configuration data for initializing and running the Proxy Server.
 *
 * @param numListeners The number of listener threads to run in the server.
 * @param numWorkers   The number of worker threads to run in the server.
 * @param portHint     A hint for the port number to bind.
 * @param portList     A comma-separated list of port numbers to bind for listener threads.
 * @param chunkSize    The size of the data chunks to be processed by the server.
 * @param autoAppend   Flag indicating whether to automatically append a NUL byte to messages.
 * @param timeout      The timeout duration for socket operations, in milliseconds.
 * @param logFilePath  The file path for logging server activity.
 *
 * @author Mrigank Kumar
 */
public record ServerConfig(
    int numListeners,
    int numWorkers,
    int portHint,
    String portList,
    int chunkSize,
    boolean autoAppend,
    int timeout,
    String logFilePath
) {}
