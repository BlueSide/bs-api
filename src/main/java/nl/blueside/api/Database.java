package nl.blueside.sp_api;

import java.util.Arrays;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;

public class Database
{
    public Database()
    {
        // Connect to database
        String hostName = "blueside.database.windows.net";
        String dbName = "sp_api";
        String user = "bsadmin";
        String password = "fKtRUKLLQrxAjK8DYc9OktaJyE1jVsTndVAzuPOoHLpoCKccZW5MGiLciB20vKyc";
        String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url);
            String schema = connection.getSchema();
            System.out.println("Successful connection - Schema: " + schema);

            System.out.println("Query data example:");
            System.out.println("=========================================");

            // Create and execute a SELECT SQL statement.
            String selectSql = "SELECT * FROM dbo.test;";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectSql))
            {
                // Print results from select statement
                System.out.println("Query results:");
                while (resultSet.next())
                {
                    System.out.println(resultSet
                                       .getString(1) + "\t"
                                       + resultSet.getString(2) + "\t"
                                       + resultSet.getString(3));
                }
                connection.close();
            }                   
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
