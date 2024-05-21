package com.cs506.project.schemas;

/**
 * This represents a Facility row in the Facility table in our 506 database.
 */
public class FacilitySchema {

    /**
     * This is the unique id for the facility queried.
     */
    public int facilityId;

    /**
     * This is the name of the facilitiy queried.
     */
    public String name;

    /**
     * This is the city for the facility queried.
     */
    public String city;

    /**
     * This is the state for the facility queried.
     */
    public String state;

    /**
     * This is the description of the facility queried.
     */
    public String description;

    /**
     * This is the count of components in production at the facility queried.
     */
    public int componentsInProduction;

    /**
     * This is the count of components completed at the facility queried.
     */
    public int componentsCompleted;

    /**
     * This is the count of models in production at the facility queried.
     */
    public int modelsInProduction;

    /**
     * This is the count of models completed at the facility queried.
     */
    public int modelsCompleted;

    /**
     * This is the count of employees at the facility queried.
     */
    public int employeeCount;

    /**
     * This is the id of the manager that manages the facility queried.
     */
    public int managerId;

}
