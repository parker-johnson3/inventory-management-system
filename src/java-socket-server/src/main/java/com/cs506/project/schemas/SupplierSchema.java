package com.cs506.project.schemas;

/**
 * This represents a Supplier row in the Supplier table in our 506 database.
 */
public class SupplierSchema {

    /**
     * This is the unique id for the supplier queried.
     */
    public int supplierId;

    /**
     * This is the name of the supplier queried.
     */
    public String name;

    /**
     * This is the description of the supplier queried.
     */
    public String description;

    /**
     * This is a comma delimited list of the component types the supplier queried supplies.
     */
    public String componentTypeList;

    /**
     * This is a comma delimited list of the IDs of the facilities that are supplied
     */
    public String facilitesSupplying;

}
