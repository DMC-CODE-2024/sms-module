/*
 * package com.ceir.CeirCode.configuration; import javax.sql.DataSource; import
 * org.springframework.beans.factory.annotation.Qualifier; import
 * org.springframework.boot.context.properties.ConfigurationProperties; import
 * org.springframework.boot.jdbc.DataSourceBuilder; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.jdbc.core.JdbcTemplate;
 * 
 * @Configuration public class DBConfiguration {
 * 
 * 
 * @Bean(name = "mysqlDb")
 * 
 * @ConfigurationProperties(prefix = "spring.datasource") public DataSource
 * mysqlDataSource() { // DataSource return DataSourceBuilder.create().build();
 * }
 * 
 * @Bean(name = "mysqlJdbcTemplate") public JdbcTemplate
 * jdbcTemplate(@Qualifier("mysqlDb") DataSource dsMySQL) { return new
 * JdbcTemplate(dsMySQL); }
 * 
 * @Bean(name = "postgresDb")
 * 
 * @ConfigurationProperties(prefix = "spring.ds.post") public DataSource
 * postgresDataSource() { return DataSourceBuilder.create().build(); }
 * 
 * @Bean(name = "postgresJdbcTemplate") public JdbcTemplate
 * postgresJdbcTemplate(@Qualifier("postgresDb") DataSource dsPostgres) {
 * 
 * return new JdbcTemplate(dsPostgres); } }
 */