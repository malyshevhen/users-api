package configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the configuration for user-related settings.
 * </p>
 * This class provides a way to configure the minimum age required for users.
 * 
 * @author Evhen Malysh
 */
@Getter
@Setter
@Configuration
public class UserConfiguration {

    /**
     * Configures the minimum age required for a user to register.
     */
    @Value("${user.min-age}")
    private int requiredAge;

}
