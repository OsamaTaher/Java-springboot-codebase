package common.management.administration.controller;

import common.management.administration.payload.request.CreateStaffRequest;
import common.management.administration.payload.request.RoleCreate;
import common.management.administration.payload.request.RoleUpdatePrivilege;
import common.management.administration.payload.request.UpdateUserRolesRequest;
import common.management.administration.service.AdministrationService;
import common.management.common.security.PermissionsEnum;
import common.management.common.util.OperationStatus;
import common.management.department.payload.request.CreateDepartmentRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static common.management.common.util.OperationStatus.OP_STATUS_SUCCESS;

@RestController
@RequestMapping("/api/v1/administration")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class AdministrationController {
    private final AdministrationService administrationService;
    private final OperationStatus operationStatus;

    /*============ STAFF ===============*/

    @PostMapping("/staff/employee")
    public ResponseEntity<?> createEmployeeByAdmin(@Valid @RequestBody CreateStaffRequest request) {
        return operationStatus.handle(administrationService.createEmployee(request));
    }


    /*================ DEPARTMENTS ===============*/
    @PostMapping("/department")
    public ResponseEntity<?> createDepartment(@Valid @RequestBody CreateDepartmentRequest request){
        return operationStatus.handle(administrationService.createDepartment(request));
    }


    /* ============ ROLES ============= */
    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleCreate request) {
        return operationStatus.handle(administrationService.createRole(request));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        var result = administrationService.getAllRoles();
        if (result.status() != OP_STATUS_SUCCESS) return operationStatus.handle(result.status());
        return ResponseEntity.ok().body(result.data());
    }

    @PutMapping("/roles/privileges")
    public ResponseEntity<?> updateRolePrivilege(@Valid @RequestBody RoleUpdatePrivilege request) {
        return operationStatus.handle(administrationService.updateRolePrivileges(request));
    }

//    @PutMapping("/roles/add/privileges")
//    public ResponseEntity<?> addPrivilegesToRole(@Valid @RequestBody RoleUpdatePrivilege request) {
//        return operationStatus.handle(administrationService.addPrivilegesToRole(request));
//    }

    @GetMapping("/roles/{roleName}/privileges")
    public ResponseEntity<?> getRolePrivileges(@PathVariable(name = "roleName") String roleName) {
        var result = administrationService.getRolePrivileges(roleName);
        if (result.status() != OP_STATUS_SUCCESS) return operationStatus.handle(result.status());
        return ResponseEntity.ok().body(result.data());
    }

    @GetMapping("/privileges")
    public ResponseEntity<?> getAllPrivileges() {
        var result = administrationService.getAllPrivileges();
        if (result.status() != OP_STATUS_SUCCESS) return operationStatus.handle(result.status());
        return ResponseEntity.ok().body(result.data());
    }

    @GetMapping("/privileges/{privilegeId}/roles")
    public ResponseEntity<?> getPrivilegeRoles(@PathVariable(name = "privilegeId") Long privilegeId) {
        var result = administrationService.getPrivilegeRoles(privilegeId);
        if (result.status() != OP_STATUS_SUCCESS) return operationStatus.handle(result.status());
        return ResponseEntity.ok().body(result.data());
    }

    @GetMapping("/roles/user/{userId}")
    public ResponseEntity<?> getUserRoles(@PathVariable(name = "userId") String userId){
        return operationStatus.handle(administrationService.getUserRoles(userId));
    }

    @PutMapping("/roles/user")
    public ResponseEntity<?> updateUserRoles(@Valid @RequestBody UpdateUserRolesRequest request){
        return operationStatus.handle(administrationService.updateUserRoles(request));
    }

    /********************** PERMISSIONS **************************/
    @PutMapping("/roles/permissions/{roleName}")
    public ResponseEntity<?> updateRolePermissions(@PathVariable(name = "roleName") String roleName, @RequestBody Set<PermissionsEnum> permissions){
        return operationStatus.handle(administrationService.updateRolePermissions(roleName,permissions));
    }

    @GetMapping("/permissions")
    public ResponseEntity<?> getAllPermissions(){
        return operationStatus.handle(administrationService.getAllPermissions());
    }

    @GetMapping("roles/permissions/{roleName}")
    public ResponseEntity<?> getRolePermissions(@PathVariable(name = "roleName") String roleName){
        return operationStatus.handle(administrationService.getRolePermissions(roleName));
    }
}
