package com.cs506.project.interfaces;

import com.cs506.project.schemas.AirplaneSchema;

import java.sql.SQLException;
import java.util.List;

/**
 * This is the generic interface which outlines general query methods used by all SQL repositories.
 */
public interface ISQLRepository<E> {

    /**
     * Fetches all rows in the table, however only certain predetermined 'basic info' columns.
     *
     * @return List of  Java Objects.
     */
    public List<E> getAllWithBasicDetails (int limit) throws SQLException;

    /**
     * Fetches all rows and columns in the E table.
     *
     * @return List of E Java Objects.
     */
    public List<E> getAllWithAllDetails (int limit) throws SQLException;

    /**
     * Fetches an E object based on the id passed.
     *
     * @param entityId : Id of the E queried.
     * @return E Java Object.
     */
    public List<E> getById (int entityId) throws SQLException;

    /**
     * Takes a Create query and funnels it to the appropriate method within class.
     *
     * @param requestEntities : List of E to create in database.
     * @return
     */
    public List<E> handleCreateQuery(List<E> requestEntities) throws SQLException;

    /**
     * Takes a Read query and funnels it to the appropriate method within class.
     *
     * @param limit : If not equal to -1, then adds a limit operator to final query.
     * @param readAllDetails : Specifies whether all columns are requested from database.
     * @param requestEntities : If specified, a getById query is ran using the entities' in the list IDs
     * @return List of E read from database;
     */
    public List<E> handleReadQuery(int limit, boolean readAllDetails, List<E> requestEntities) throws SQLException;

    /**
     * Takes a Update query and funnels it to the appropriate method within class.
     *
     * @param request : List of E to update database with.
     * @return List of updated Es
     */
    public List<E> handleUpdateQuery(List<E> request) throws SQLException;

    /**
     * Takes a Delete query and funnels it to the appropriate method within class.
     *
     * @param request : List of E to delete from database.
     * @return List of deleted Es
     */
    public List<E> handleDeleteQuery(List<E> request) throws SQLException;

}
