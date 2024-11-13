package common.management.common.payload.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordFromAdminRequest(
        @NotBlank
        String newPassword,
        @NotBlank
        String confirmNewPassword
) {
}
