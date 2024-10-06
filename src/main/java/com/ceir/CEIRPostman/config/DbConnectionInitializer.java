package com.ceir.CEIRPostman.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DbConnectionInitializer implements CommandLineRunner {

    @Autowired
    private DbConnectionChecker dbConnectionChecker;

    @Autowired
    @Qualifier("appDataSource")
    private DataSource appDataSource;

    @Override
    public void run(String... args) {
        dbConnectionChecker.checkDbConnection(appDataSource, "app");
        dbConnectionChecker.checkDbConnection(appDataSource, "aud");
    }
}

