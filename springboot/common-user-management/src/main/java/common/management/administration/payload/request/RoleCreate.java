package common.management.administration.payload.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record RoleCreate(
        @NotBlank
        String name,
        String nameAr,
        Set<Long> privileges
){
}
