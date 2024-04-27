package com.github.malyshevhen.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import lombok.Setter;

/**
 * This class configures a Spring test application context with a user constraint bean.
 * The user constraint bean returns the minimum required age, which is loaded from the
 * `user.min-age` property.
 * <p/>
 * This configuration is only active when the "test" profile is enabled.
 */
@Profile("test")
@Setter
@TestConfiguration
public class TestApplicationConfig {

    @Value("${user.min-age}")
    private int requiredAge;

    @Bean
    UserConstraints userConstraints() {
        return () -> requiredAge;
    }

    /**
     * Provides a test-specific DataSource for the application.
     * <p/>
     * This DataSource is configured to use a PostgreSQL database provided by the
     * Testcontainers library.
     * <p/>
     * The database connection details are hardcoded for the test environment.
     */
    @Bean
    DataSource getDataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:tc:postgresql:16-alpine:///test_db");
        dataSourceBuilder.username("testuser");
        dataSourceBuilder.password("testpass");
        return dataSourceBuilder.build();
    }
}
