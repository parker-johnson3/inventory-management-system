package com.cs506.project.schemas;

/**
 * This represents a many to many relationship of suppliers to facilities.
 */
public class SupplierFacilitySchema {

    /**
     * This is the row number in the table.
     */
    public int id;

    public int supplierId;

    public int facilityId;

}
