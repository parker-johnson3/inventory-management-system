package com.cs506.project.schemas;

import java.util.Date;

/**
 * This class represents an Airplane row in the Airplane table in our 506 database.
 */
public class AirplaneSchema {

    /**
     * This is the unique id of the airplane queried.
     */
    public int airplaneId;

    /**
     * This is the name of the airplane queried.
     */
    public String name;

    /**
     * This is the description of the plane queried
     */
    public String description;

    /**
     *This is the city of the plane queried
     */
    public String city;

    /**
     * This is the state of the plane queried
     */
    public String state;

    /**
     * This is the stage of production the airplane queried is currently in.
     */
    public String productionStage;

    /**
     * This is the total cost of producing the airplane queried.
     */
    public double cost;

    /**
     * This is the date production of the airplane queried started.
     */
    public Date dateStarted;

    /**
     * This is the date production of the airplane queried ceased.
     */
    public Date dateFinished;

    /**
     * This is the id of the facility that contains the airplane.
     */
    public int facilityID;

    /**
     * This is the seating capacity of airplane queried
     */
    public int seatingCapacity;

    /**
     * This is the size category of the airplane queried.
     */
    public String size;

    /**
     * This indicates whether the airplane queried contains first class seating.
     */
    public boolean hasFirstClass;

}
