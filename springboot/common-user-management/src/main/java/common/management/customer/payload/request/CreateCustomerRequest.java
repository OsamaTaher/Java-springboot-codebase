package common.management.customer.payload.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest (
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @NotBlank
    String phone,
    @NotBlank
    String password,
    @NotBlank
    String confirmPassword
    ){
}
