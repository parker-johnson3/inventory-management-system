package com.cs506.project.server;

/**
 * Represents a task to be executed by a proxy server.
 *
 * This functional interface defines a single method, `handleRequest`,
 * which takes a byte array representing a request and returns a string
 * representing the response to the request.
 *
 * Implementations of this interface should provide the logic for handling
 * incoming requests and generating appropriate responses.
 *
 * @author Mrigank Kumar
 */
@FunctionalInterface
public interface ProxyServerTask {
    /**
     * Handles an incoming request and generates a response.
     *
     * @param request the byte array representing the incoming request
     * @return a string representing the response to the request
     */
    public String handle(byte[] request);
}
