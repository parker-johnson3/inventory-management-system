package com.cs506.project.schemas;

/**
 * This represents a Manager in the Manager table in our 506 database.
 */
public class ManagerSchema {

    /**
     * This is the unique id for the manager queried.
     */
    public int managerId;

    /**
     * This is the name of the manager queried.
     */
    public String name;

    /**
     * This is the password for the manager queried.
     */
    public String password;

    /**
     * This is the position title for the manager queried.
     */
    public String position;

    /**
     * This is the access tier level the manager queried has.
     */
    public int accessLevel;

    /**
     * This is the id of the facility the manager queried manages.
     */
    public int facilityId;

}
