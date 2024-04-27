package com.github.malyshevhen.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Global application configuration.
 * 
 * @author Evhen Malysh
 */
@Profile("!test")
@Configuration
public class ApplicationConfig {

    @Value("${user.min-age}")
    private int requiredAge;

    @Value("${spring.datasource.url}")
    private String dbURL;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    /**
     * Returns a {@link UserConstraints} instance that enforces the required age
     * constraint.
     *
     * @return a {@link UserConstraints} instance that enforces the required age
     *         constraint
     */
    @Bean
    UserConstraints userConstraints() {
        return () -> requiredAge;
    }

    @Bean
    DataSource getDataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(dbURL);
        dataSourceBuilder.username(dbUsername);
        dataSourceBuilder.password(dbPassword);
        return dataSourceBuilder.build();
    }

}
