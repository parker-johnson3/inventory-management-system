package com.cs506.project;
import java.sql.*;


/*
 * JDBConnection class that establishes a connection with the database and then returns that connection
 * */
public class JDBCConnection {
    private static final String SQLHOSTNAME = System.getenv().getOrDefault("SQL_SERVER_HOST", "localhost");
    private static final String SQLPORTNAME = System.getenv().getOrDefault("SQL_SERVER_PORT", "3306");
    private static final String DB_URL = "jdbc:mysql://" + SQLHOSTNAME + ":" + SQLPORTNAME + "/testdb1";
    private static final String USER = System.getenv().getOrDefault("USERNAME","user");
    private static final String PASS = System.getenv().getOrDefault("PASSWORD","password");
    private static Connection connection;
    /**
     * Actual connection method that will use the variables above and if it succeeded will return a connection
     *
     * @return connection object
     */
    public static Connection getConnection(){
        if (connection == null){
            try{
                //registers the JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                // opens the connection
                connection = DriverManager.getConnection(DB_URL,USER,PASS);
            } catch(Exception e ){
                e.printStackTrace();
            }
        }
        return connection;
    }
}
