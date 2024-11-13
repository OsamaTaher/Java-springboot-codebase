package common.management.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix="usermanagement")
@Component
@Getter
@Setter
public class ExternalConfig {
    // user managment library error messages with defaults
    private String usernameTaken = "Username is already taken";
    private String refreshTokenCreationFail = "Failed to create new refresh token!";
    private String refreshTokenNotValid = "Refresh Token Not Valid";
    private String securityForbidden = "Forbidden";
    private String securityUnauthorized = "Unauthorized";

    // user management library JWT token configurations
    private String jwtSecret;

    private Long jwtExpirationMs = 60000L;

    //Refresh token config
    private Long RefreshTokenExpirationMs = 60000L;

    //Web security configuration
    private String[] noSecureUrl;

    private String[] allowedForAllRoles;

}
