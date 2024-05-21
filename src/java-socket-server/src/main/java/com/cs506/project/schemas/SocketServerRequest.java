package com.cs506.project.schemas;

import java.util.List;

/**
 * This is the Java representation of a request the socket server will receive.
 */
public class SocketServerRequest {

    /**
     * This represents the type of request being made.
     * Valid requests are: Create, Read, Update, Delete
     */
    public String type;

    /**
     * This represents the entity or table that will be acted upon
     */
    public String entityName;

    /**
     * If the request is a read, this is how many rows are being requested.
     * This should be made -1 if all are being requested
     */
    public int limit;

    /**
     * This is a boolean control on whether all details of an entity are being requested.
     */
    public boolean requestingAllDetails;

    /**
     * If the request is a Create or Update, this will be a list of all the rows needing to be created/updated.
     * If the request is a Read by ID or Delete, this should contain the corresponding entity with its ID filled in.
     */
    public List<Object> entities;

}
