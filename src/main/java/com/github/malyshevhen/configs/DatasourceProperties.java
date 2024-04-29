package com.github.malyshevhen.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Datasource configuration properties
 *
 * @author Evhen Malysh
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceProperties {
    private String url;
    private String username;
    private String password;
}
