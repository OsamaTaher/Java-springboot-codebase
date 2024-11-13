package common.management.common.security;

import common.management.administration.payload.response.RoleDto;
import common.management.common.model.Privilege;
import common.management.common.model.Role;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RoleCache {
    void initRoleCache();

    void refreshRoleRequestMatcher(Role role);

    void insertRole(Role role);

    void refreshRolePrivileges(Role role, List<Privilege> privileges);

    List<RoleDto> getDefinedRoles();

    Optional<Long> getRoleId(String roleName);

    List<Privilege> getRolePrivilege(String roleName);

    Map<String, List<RequestMatcher>> getRequestMatchers();

    List<String> getRolePermissions(String roleName);

    boolean containsRole(String roleName);

    void updateRolePermissions(String roleName, Set<PermissionsEnum> permissions);
}
