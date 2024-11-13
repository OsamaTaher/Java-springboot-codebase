package common.management.common.payload.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record LoginResponse(
        @NotBlank
        String token,
        @NotBlank
        String type,
        @NotBlank
        String id,
        @NotBlank
        String username,
        @NotNull
        List<String> roles,
        @NotBlank
        String refreshToken
        ) {
}
