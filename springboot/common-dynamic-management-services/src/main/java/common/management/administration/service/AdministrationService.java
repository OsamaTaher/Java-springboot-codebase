package common.management.administration.service;

import common.management.administration.payload.request.CreateStaffRequest;
import common.management.administration.payload.request.RoleCreate;
import common.management.administration.payload.request.RoleUpdatePrivilege;
import common.management.administration.payload.request.UpdateUserRolesRequest;
import common.management.administration.payload.response.RoleDto;
import common.management.administration.payload.response.RolePermissionsDto;
import common.management.common.model.Privilege;
import common.management.common.model.Role;
import common.management.common.security.PermissionsEnum;
import common.management.common.util.OpWrapper;
import common.management.department.payload.request.CreateDepartmentRequest;

import java.util.List;
import java.util.Set;

public interface AdministrationService {
    int createEmployee(CreateStaffRequest request);

    /*=============== DEPARTMENTS ===============*/
    OpWrapper<Long> createDepartment(CreateDepartmentRequest request);

    /*=============== ROLES ===============*/
    int createRole(RoleCreate request);

    int updateRolePrivileges(RoleUpdatePrivilege request);

    int addPrivilegesToRole(RoleUpdatePrivilege request);

    OpWrapper<List<RoleDto>> getAllRoles();

    OpWrapper<List<Privilege>> getAllPrivileges();

    OpWrapper<List<Privilege>> getRolePrivileges(String roleName);

    OpWrapper<List<Role>> getPrivilegeRoles(Long privilegeId);

    int updateUserRoles(UpdateUserRolesRequest request);

    OpWrapper<List<RoleDto>> getUserRoles(String userId);

    int updateRolePermissions(String roleName, Set<PermissionsEnum> permissions);

    OpWrapper<RolePermissionsDto> getRolePermissions(String roleName);

    OpWrapper<List<String>> getAllPermissions();
}
