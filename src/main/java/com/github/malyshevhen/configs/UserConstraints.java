package com.github.malyshevhen.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Defines the required age constraint for users.
 * 
 * @author Evhen Malysh
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "validation-constraints.user")
public class UserConstraints {
    private int requiredAge;
}
