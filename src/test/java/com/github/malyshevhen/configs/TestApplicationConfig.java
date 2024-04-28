package com.github.malyshevhen.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import lombok.Setter;

/**
 * This class configures a Spring test application context with a user
 * constraint bean.
 * The user constraint bean returns the minimum required age, which is loaded
 * from the
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

    @Value("${spring.datasource.url}")
    private String dbURL;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    UserConstraints userConstraints() {
        return () -> requiredAge;
    }

    /**
     * Provides a test-specific DataSource for the application.
     */
    @Bean
    DataSource getDataSource() {
        return DataSourceBuilder.create()
                .url(dbURL)
                .username(dbUsername)
                .password(dbPassword)
                .build();
    }
}
