package com.cs506.project.configs;

/**
 * Represents the configuration settings for a worker thread in the Proxy Server.
 *
 * This record encapsulates parameters such as chunk size, timeout duration,
 * and whether to automatically append a NUL byte to messages processed by the
 * worker.
 *
 * Instances of WorkerConfig are immutable and can be used to store
 * configuration data for initializing worker threads in the Proxy Server.
 *
 * @param chunkSize  The size of the data chunks to be processed by the worker.
 * @param timeout    The timeout duration for socket operations, in milliseconds.
 * @param autoAppend A flag indicating whether to automatically append a
 *                   NUL byte to messages.
 *
 * @author Mrigank Kumar
 */
public record WorkerConfig(int chunkSize, int timeout, boolean autoAppend) {}
