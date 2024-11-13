package common.management.common.payload.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record SignupRequest(
        @NotBlank
        String username,
        @NotBlank
        String password,
        Set<String> roles
) {
}
