package com.ceir.CEIRPostman.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.ceir.CEIRPostman.Repository.audit"},
        entityManagerFactoryRef = "auditEntityManagerFactory",
        transactionManagerRef = "auditTransactionManager")
@EntityScan( "com.ceir.CEIRPostman.model.audit" )

public class AuditDbConfig {
    @Autowired
    Environment env;
    @Autowired
    private DbConnectionChecker dbConnectionChecker;

    @PostConstruct
    public void checkDbConnection() {
        dbConnectionChecker.checkDbConnection(auditDataSource(), "aud");
    }

    @Bean(name = "auditEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean auditEntityManagerFactory(
            @Qualifier("auditDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("com.ceir.CEIRPostman.model.audit")
                .persistenceUnit("aud")
                .properties(jpaProperties())
                .build();
    }

    @Bean(name = "auditDataSource")
//    @ConfigurationProperties(prefix = "audit.datasource")
    public DataSource auditDataSource() {
        if ("oracle".equals(env.getProperty("spring.profiles.active"))) {
            return DataSourceBuilder.create()
                    .url(env.getProperty("audit.datasource.oracle.url"))
                    .username(env.getProperty("audit.datasource.oracle.username"))
                    .password(env.getProperty("audit.datasource.oracle.password"))
                    .driverClassName(env.getProperty("audit.datasource.oracle.driver-class-name"))
                    .build();
        } else {
            return DataSourceBuilder.create()
                    .url(env.getProperty("audit.datasource.url"))
                    .username(env.getProperty("audit.datasource.username"))
                    .password(env.getProperty("audit.datasource.password"))
                    .driverClassName(env.getProperty("audit.datasource.driver-class-name"))
                    .build();
        }
    }

    @Bean(name = "auditTransactionManager")
    public PlatformTransactionManager auditTransactionManager(
            @Qualifier("auditEntityManagerFactory") LocalContainerEntityManagerFactoryBean auditEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(auditEntityManagerFactory.getObject()));
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
