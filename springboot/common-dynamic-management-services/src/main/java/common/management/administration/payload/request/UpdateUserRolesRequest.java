package common.management.administration.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record UpdateUserRolesRequest(
        @NotEmpty Set<Long> roles, @NotBlank String userId
)
{
}
