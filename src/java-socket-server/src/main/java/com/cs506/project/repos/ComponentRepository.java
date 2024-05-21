package com.cs506.project.repos;

import com.cs506.project.interfaces.ISQLRepository;
import com.cs506.project.schemas.ComponentSchema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL Repository used to query the Component table in the 506 database.
 */
public class ComponentRepository implements ISQLRepository<ComponentSchema> {

    private Connection connection;
    
    public ComponentRepository (Connection connection) {
        // Run jdbcConnection.createConnection(): Each method will be responsible for closing the connection
        this.connection = connection;
    }

    /**
     * Fetches all rows in the table, however only certain predetermined 'basic info' columns.
     *
     * @return List of  Java Objects.
     */
    @Override
    public List<ComponentSchema> getAllWithBasicDetails(int limit) throws SQLException{
        List<ComponentSchema> components = new ArrayList<>();

        String query = "";
        if (limit != -1) {
            query = "SELECT ComponentId, Name, ProductionStage, Cost FROM Component LIMIT " + limit;
        } else {
            query = "SELECT ComponentId, Name, ProductionStage, Cost FROM Component";
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {

                ComponentSchema componentSchema = new ComponentSchema();

                componentSchema.componentId = resultSet.getInt("ComponentId");
                componentSchema.name = resultSet.getString("Name");
                componentSchema.cost = resultSet.getDouble("Cost");
                componentSchema.productionStage = resultSet.getString("ProductionStage");

                components.add(componentSchema);
            }

        }

        return components;

    }

    /**
     * Fetches all rows and columns in the Component table.
     *
     * @return List of Component Java Objects.
     */
    @Override
    public List<ComponentSchema> getAllWithAllDetails(int limit) throws SQLException {
        List<ComponentSchema> components = new ArrayList<>();

        String query = "";

        if (limit != -1){
            query = "SELECT * FROM Component LIMIT " + limit;
        } else {
            query = "SELECT * FROM Component";
        }


        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {

                ComponentSchema componentSchema = new ComponentSchema();

                componentSchema.componentId = resultSet.getInt("ComponentId");
                componentSchema.city = resultSet.getString("City");
                componentSchema.state = resultSet.getString("State");
                componentSchema.name = resultSet.getString("Name");
                componentSchema.description = resultSet.getString("Description");
                componentSchema.componentType = resultSet.getString("ComponentType");
                componentSchema.facilityId = resultSet.getInt("FacilityId");
                componentSchema.cost = resultSet.getDouble("Cost");
                componentSchema.productionStage = resultSet.getString("ProductionStage");

                components.add(componentSchema);
            }

        }

        return components;
    }

    /**
     * Fetches an E object based on the id passed.
     *
     * @param componentId : Id of the Component queried.
     * @return Component Java Object.
     */
    @Override
    public List<ComponentSchema> getById(int componentId) throws SQLException{
        List<ComponentSchema> component = new ArrayList<>();
        String query = "SELECT * FROM Component WHERE ComponentId = " + componentId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)){

            while (resultSet.next()) {
                
                ComponentSchema componentSchema = new ComponentSchema();

                componentSchema.componentId = resultSet.getInt("ComponentId");
                componentSchema.city = resultSet.getString("City");
                componentSchema.state = resultSet.getString("State");
                componentSchema.name = resultSet.getString("Name");
                componentSchema.description = resultSet.getString("Description");
                componentSchema.componentType = resultSet.getString("ComponentType");
                componentSchema.facilityId = resultSet.getInt("FacilityId");
                componentSchema.cost = resultSet.getDouble("Cost");
                componentSchema.productionStage = resultSet.getString("ProductionStage");

                component.add(componentSchema);
                
            }
            
        }
        
        return component;
    }

    /**
     * Takes a Create query and funnels it to the appropriate method within class.
     *
     * @param requestEntities : List of Components to create in database.
     * @return
     */
    @Override
    public List<ComponentSchema> handleCreateQuery(List<ComponentSchema> requestEntities) throws SQLException {
        
        return null;
    }

    /**
     * Takes a Read query and funnels it to the appropriate method within class.
     *
     * @param limit          : If not equal to -1, then adds a limit operator to final query.
     * @param readAllDetails : Specifies whether all columns are requested from database.
     * @return List of Components read from database;
     */
    @Override
    public List<ComponentSchema> handleReadQuery(int limit, boolean readAllDetails, List<ComponentSchema> components) throws SQLException {
        
        List<ComponentSchema> result = new ArrayList<>();


        if (components.size() != 0){
            for ( ComponentSchema component : components ){
                result.addAll(getById(component.componentId));
            }
        } else if (!readAllDetails) {
            result = getAllWithBasicDetails(limit);
        } else {
            result = getAllWithAllDetails(limit);
        }
        
        return result;
    }

    /**
     * Takes a Update query and funnels it to the appropriate method within class.
     *
     * @param request : List of E to update database with.
     * @return List of updated Es
     */
    @Override
    public List<ComponentSchema> handleUpdateQuery(List<ComponentSchema> request) throws SQLException {
        
        return null;
    }

    /**
     * Takes a Delete query and funnels it to the appropriate method within class.
     *
     * @param request : List of Components to delete from database.
     * @return List of deleted Components
     */
    @Override
    public List<ComponentSchema> handleDeleteQuery(List<ComponentSchema> request) throws SQLException {
        
        return null;
    }

}
