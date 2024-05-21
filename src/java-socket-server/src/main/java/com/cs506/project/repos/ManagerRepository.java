package com.cs506.project.repos;

import com.cs506.project.JDBCConnection;
import com.cs506.project.interfaces.ISQLRepository;
import com.cs506.project.schemas.ManagerSchema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class ManagerRepository implements ISQLRepository<ManagerSchema>{
    private Connection connection;

    public ManagerRepository (Connection connection) throws SQLException {
        this.connection = connection;
    }

    /**
     * The basic details will get the ID, Name, Position, and Access Level columns
     *
     * @return List of Manager Java Objects with only the basic details
     * */
    @Override
    public List<ManagerSchema> getAllWithBasicDetails(int limit) throws SQLException {
        List<ManagerSchema> manager = new ArrayList<>();
        String query = "";
        if (limit != -1) {
            query = "SELECT managerId, name, position, accessLevel FROM Manager LIMIT " + limit;
        } else {
            query = "SELECT managerId, name, position, accessLevel FROM Manager";
        }
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)){
            while(resultSet.next()){
                ManagerSchema managerSchema = new ManagerSchema();

                managerSchema.managerId = resultSet.getInt("ManagerID");
                managerSchema.name = resultSet.getString("Name");
                managerSchema.position = resultSet.getString("Position");
                managerSchema.accessLevel = resultSet.getInt("AccessLevel");

                manager.add(managerSchema);
            }
        }
        return manager;
    }

    /**
     * Fetches all rows and columns in the Manager table.
     *
     * @return List of Manager Java Objects.
     */
    @Override
    public List<ManagerSchema> getAllWithAllDetails(int limit) throws SQLException {
        List<ManagerSchema> facilities = new ArrayList<>();
        String query = "";
        if(limit != -1){
            query = "SELECT * FROM Manager LIMIT " + limit;
        } else {
            query = "SELECT * FROM Manager";
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {

                ManagerSchema managerSchema= new ManagerSchema();

                managerSchema.managerId = resultSet.getInt("ManagerID");
                managerSchema.name = resultSet.getString("Name");
                managerSchema.password = resultSet.getString("Password");
                managerSchema.position = resultSet.getString("Position");
                managerSchema.accessLevel = resultSet.getInt("AccessLevel");
                managerSchema.facilityId = resultSet.getInt("FacilityID");

                facilities.add(managerSchema);
            }
        }

        return facilities;
    }

    /**
     * Fetches an all data for Manager based on the id passed.
     *
     * @param managerId : Id of the Manager queried.
     * @return Manager Java Object.
     */
    @Override
    public List<ManagerSchema> getById(int managerId) throws SQLException{
        List<ManagerSchema> manager = new ArrayList<>();

        String query = "SELECT * FROM Manager WHERE managerId = " + managerId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);){

            while (resultSet.next()) {

                ManagerSchema managerSchema = new ManagerSchema();

                managerSchema.managerId = resultSet.getInt("ManagerID");
                managerSchema.name = resultSet.getString("Name");
                managerSchema.password = resultSet.getString("Password");
                managerSchema.position = resultSet.getString("Position");
                managerSchema.accessLevel = resultSet.getInt("AccessLevel");
                managerSchema.facilityId = resultSet.getInt("FacilityID");

                manager.add(managerSchema);

            }
        }

        return manager;
    }

    /**
     * Adds a manager into the database
     *
     * @param managerSchema : The manager that needs to be added to the database
     * @return result : returns true if successful
     */
    public List<ManagerSchema> handleAddManager(List<ManagerSchema> managerSchema) throws SQLException{
        try(Statement statement = connection.createStatement();) {
            for (ManagerSchema managerSchema1 : managerSchema) {
                String query = "INSERT INTO appdb"
                        + "(managerId, Name, Password, Position, AccessLevel, FacilityID)"
                        + "VALUES (" + managerSchema1.managerId + ", "
                        + managerSchema1.name + ", "
                        + managerSchema1.password + ", "
                        + managerSchema1.position + ", "
                        + managerSchema1.accessLevel + ", "
                        + managerSchema1.facilityId + ")";
                statement.executeQuery(query);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Takes a Create query and funnels it to the appropriate method within class.
     *
     * @param requestEntities : List of Facilities to create in database
     * @return
     *
     * */
    @Override
    public List<ManagerSchema> handleCreateQuery(List<ManagerSchema> requestEntities) throws SQLException {
        return null;
    }

    /**
     * Handles a Read query and sends it to the correct method within class.
     *
     * @param limit          : If not equal to -1, then adds a limit operator to final query.
     * @param readAllDetails : Specifies whether all columns are requested from database.
     * @return List of E read from database;
     *
     * */
    @Override
    public List<ManagerSchema> handleReadQuery(int limit, boolean readAllDetails, List<ManagerSchema> facilties) throws SQLException {
        List<ManagerSchema> result = new ArrayList<>();

        if(facilties.size() != 0){
            for(ManagerSchema manager : facilties){
                result.addAll(getById(manager.managerId));
            }
        }
        if(!readAllDetails){
            result = getAllWithBasicDetails(limit);
        } else{
            result = getAllWithAllDetails(limit);
        }

        return result;
    }

    /**
     * Handles the update query and sends it to the correct method in class
     *
     * @param request : list of manager that need to be updated in the database
     * @return Updated list of manager
     * */
    @Override
    public List<ManagerSchema> handleUpdateQuery(List<ManagerSchema> request) throws SQLException {
        return null;
    }

    /**
     * Handles the delete query and sends it to the correct method in class
     *
     * @param request : list of manager that need to be deleted from the database
     * @return List of Airplanes removed
     * */
    @Override
    public List<ManagerSchema> handleDeleteQuery(List<ManagerSchema> request) throws SQLException {

        return null;
    }
}
