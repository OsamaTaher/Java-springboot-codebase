package common.management.administration.payload.request;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RoleUpdatePrivilege (
    @NotNull
    Long roleId,
    @NotNull
    Set<Long> privileges
) {}
