package com.ceir.CEIRPostman.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.ceir.CEIRPostman.Repository.app"},
        entityManagerFactoryRef = "appEntityManagerFactory",
        transactionManagerRef = "appTransactionManager")
@EntityScan("com.ceir.CEIRPostman.model.app")

public class AppDbConfig {
    @Autowired
    private Environment env;
    @Autowired
    private DbConnectionChecker dbConnectionChecker;

    @PostConstruct
    public void checkDbConnection() {
        dbConnectionChecker.checkDbConnection(appDataSource(), "app");
    }

    @Primary
    @Bean(name = "appEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean appEntityManagerFactory(
            @Qualifier("appDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("com.ceir.CEIRPostman.model.app")
                .persistenceUnit("app") // CHANGE TO CEIR
                .properties(jpaProperties())
                .build();

    }

    @Primary
    @Bean(name = "appTransactionManager")
    public PlatformTransactionManager appTransactionManager(
            @Qualifier("appEntityManagerFactory") LocalContainerEntityManagerFactoryBean appEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(appEntityManagerFactory.getObject()));
    }

    // DataSource Configs
    @Primary
    @Bean(name = "appDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource appDataSource() {
        if ("oracle".equals(env.getProperty("spring.profiles.active"))) {
            return DataSourceBuilder.create()
                    .url(env.getProperty("spring.datasource.oracle.url"))
                    .username(env.getProperty("spring.datasource.oracle.username"))
                    .password(env.getProperty("spring.datasource.oracle.password"))
                    .driverClassName(env.getProperty("spring.datasource.oracle.driver-class-name"))
                    .build();
        } else {
            return DataSourceBuilder.create()
                    .url(env.getProperty("spring.datasource.url"))
                    .username(env.getProperty("spring.datasource.username"))
                    .password(env.getProperty("spring.datasource.password"))
                    .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                    .build();
        }
    }


    protected Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        String activeProfile = env.getProperty("spring.profiles.active");
        if ("oracle".equals(activeProfile)) {
            props.put("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
        } else {
            props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        }
        return props;
    }

}
