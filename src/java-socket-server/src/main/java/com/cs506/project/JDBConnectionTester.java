package com.cs506.project;


import java.sql.*;


import static com.cs506.project.JDBCConnection.getConnection;


public class JDBConnectionTester {
    /*
     * Main method runs a quick test query to make sure the connection is fine
     * */
    public static void main(String args[]) throws SQLException {


        Connection connection = getConnection();
        if(connection != null){
            System.out.println("Connection work");
        }
           /*
           connection = getConnection();
           System.out.println("got connection");
           statement = connection.createStatement();
           //String query = "Select * FROM test_table";
           //resultSet = statement.executeQuery(query);


           // Process the result set if needed
           //while (resultSet.next()) {
           // Process each row of the result set
           // Example: String name = resultSet.getString("name");
           //          int age = resultSet.getInt("age");
           //          System.out.println(name + " - " + age);
           //}


            */
    }
}
