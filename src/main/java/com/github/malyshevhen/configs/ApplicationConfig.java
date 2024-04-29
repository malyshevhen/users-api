package com.github.malyshevhen.configs;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Global application configuration.
 * 
 * @author Evhen Malysh
 */
@Profile("!test")
@Configuration
@EnableConfigurationProperties
public class ApplicationConfig {

    @Bean
    UserConstraints userConstraints() {
        return new UserConstraints();
    }

    @Bean
    DatasourceProperties datasourceProperties() {
        return new DatasourceProperties();
    }

    @Bean
    DataSource getDataSource(DatasourceProperties datasourceProperties) {
        return DataSourceBuilder.create()
                .url(datasourceProperties.getUrl())
                .username(datasourceProperties.getUsername())
                .password(datasourceProperties.getPassword())
                .build();
    }

}
