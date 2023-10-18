package com.ceir.CEIRPostman.config;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

@Component
public class DbConnectionChecker {

    public void checkDbConnection(DataSource dataSource, String dbName) {
        try (Connection connection = dataSource.getConnection()) {
            // If the connection is successful, print a log message
            System.out.println("Database connection for " + dbName + " is successful!");
        } catch (SQLException e) {
            // If there is an error obtaining the connection, print an error log
            System.err.println("Alert1201: Database connection for " + dbName + " failed or login failed due to credentials");
            e.printStackTrace();
        }
    }
}