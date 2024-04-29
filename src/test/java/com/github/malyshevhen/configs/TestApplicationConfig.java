package com.github.malyshevhen.configs;

import lombok.Setter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

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
@EnableConfigurationProperties
public class TestApplicationConfig {

    @Bean
    UserConstraints userConstraints() {
        return new UserConstraints();
    }

    @Bean
    DatasourceProperties datasourceProperties() {
        return new DatasourceProperties();
    }

    /**
     * Provides a test-specific DataSource for the application.
     */
    @Bean
    DataSource getDataSource(DatasourceProperties datasourceProperties) {
        return DataSourceBuilder.create()
                .url(datasourceProperties.getUrl())
                .username(datasourceProperties.getUsername())
                .password(datasourceProperties.getPassword())
                .build();
    }
}
