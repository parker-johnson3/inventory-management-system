package com.cs506.project.schemas;

/**
 * This represents a Component row in the Component table in our 506 database.
 */
public class ComponentSchema {

    /**
     * This is the unique id for the component queried.
     */
    public int componentId;

    /**
     * This is the name of the component queried.
     */
    public String name;

    /**
     * This is the city of the component queried
     */
    public String city;

    /**
     * This is the state of the component queried
     */
    public String state;

    /**
     * This is the description of the component queried.
     */
    public String description;

    /**
     * This is the type of the component queried.
     */
    public String componentType;

    /**
     * This is the id of the supplier that supplied the component queried.
     */
    public int facilityId;

    /**
     * This is the total cost of the component queried.
     */
    public double cost;

    /**
     * This is the current stage of production the component queried is in.
     */
    public String productionStage;

}
