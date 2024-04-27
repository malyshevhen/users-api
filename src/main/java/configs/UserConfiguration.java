package configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
public class UserConfiguration {

    @Value("${user.min-age}")
    private int requiredAge;

}
