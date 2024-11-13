package common.management.administration.payload.request;

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
        String confirmPassword
) {

        public common.management.staff.payload.request.CreateStaffRequest toCreateStaffRequest(String roleName){
                return new common.management.staff.payload.request.CreateStaffRequest(firstName,lastName,phone,password,confirmPassword,roleName);
        }
}
