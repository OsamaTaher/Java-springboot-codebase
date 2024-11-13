package common.management.administration.payload.response;

import java.util.List;

public record RolePermissionsDto (
        Long id,
        String name,
        List<String> permissions
){
}
