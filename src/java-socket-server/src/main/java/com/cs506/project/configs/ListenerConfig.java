package com.cs506.project.configs;

/**
 * Represents the configuration settings for a listener in the Proxy Server.
 *
 * This record encapsulates the port number on which the listener will listen
 * for incoming client connections.
 *
 * Instances of ListenerConfig are immutable and can be used to store
 * configuration data for initializing listener threads in the Proxy Server.
 *
 * @param port The port number on which the listener will listen for incoming
 *             connections.
 *
 * @author Mrigank Kumar
 */
public record ListenerConfig(int port) {}
