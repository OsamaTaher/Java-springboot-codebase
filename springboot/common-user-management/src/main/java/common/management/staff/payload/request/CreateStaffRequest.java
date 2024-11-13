package common.management.staff.payload.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record CreateStaffRequest(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String phone,
        @NotBlank
        String password,
        @NotBlank
        String confirmPassword,
        @NotBlank
        String role
    )
{
    @AssertTrue
    public boolean isPasswordMatch(){
        return password.equals(confirmPassword);
    }
}
