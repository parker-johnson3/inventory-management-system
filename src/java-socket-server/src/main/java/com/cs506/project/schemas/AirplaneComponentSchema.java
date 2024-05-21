package com.cs506.project.schemas;

/**
 * This represents a 1 to many relationship of airplanes to components.
 */
public class AirplaneComponentSchema {

    /**
     * This is the row number in the table.
     */
    public int id;

    public int airplaneId;

    public int componentId;

}
