package common.management.administration.service.impl;

import common.management.administration.payload.request.CreateStaffRequest;
import common.management.administration.payload.request.RoleCreate;
import common.management.administration.payload.request.RoleUpdatePrivilege;
import common.management.administration.payload.request.UpdateUserRolesRequest;
import common.management.administration.payload.response.RoleDto;
import common.management.administration.payload.response.RolePermissionsDto;
import common.management.administration.service.*;
import common.management.common.model.Privilege;
import common.management.common.model.PrivilegeRepository;
import common.management.common.model.Role;
import common.management.common.repository.RoleRepository;
import common.management.common.security.PermissionsEnum;
import common.management.common.security.RoleCache;
import common.management.common.service.UserService;
import common.management.common.util.OpWrapper;
import common.management.common.util.RoleType;
import common.management.department.payload.request.CreateDepartmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static common.management.common.util.OperationStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdministrationServiceImpl implements AdministrationService {
    private final AdministrateStaffService staffService;
    private final RoleCache roleCache;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final AdminDepartmentService adminDepartmentService;
    private final UserService userService;

    @Override
    @Transactional
    public int createEmployee(CreateStaffRequest request){
        var staff = staffService.create(request.toCreateStaffRequest(RoleType.ROLE_EMPLOYEE.name()));
        return staff.status();
    }

    /*=============== DEPARTMENTS ===============*/
    @Override
    public OpWrapper<Long> createDepartment(CreateDepartmentRequest request){
        return adminDepartmentService.create(request);
    }

    /**** ROLES AND PRIVILEGES ****/
    @Override
    public int createRole(RoleCreate request){
        try{
            String roleName;
            if(request.name().startsWith("ROLE_")){
                roleName = "ROLE_"+request.name();
            }else{
                roleName = request.name();
            }

            if(roleCache.containsRole(roleName)){
                return OP_STATUS_ROLE_EXIST;
            }
            Role role = new Role();
            role.setName(roleName);
            //CHECK PRIVILEGES
            if(request.privileges()!=null){
                List<Privilege> privilegeList = checkPrivilege(request.privileges());
                role.setPrivileges(privilegeList);
            }
            roleRepository.save(role);
            //UPDATE CACHE
            roleCache.insertRole(role);

            return OP_STATUS_SUCCESS;
        }catch (Exception e){
            log.error("[EXCEPTION] createRole: {},{},{}",e.getMessage(),e.getCause(),e);
            return OP_STATUS_FAILED;
        }
    }

    @Override
    @Transactional
    public int updateRolePrivileges(RoleUpdatePrivilege request){
        try {
            Optional<Role> role = roleRepository.findById(request.roleId());
            if (role.isEmpty()) {
                return OP_STATUS_ROLE_NOT_FOUND;
            }

            List<Privilege> privilegeList = checkPrivilege(request.privileges());
            role.get().setPrivileges(privilegeList);

            //REFRESH ROLES
            roleCache.refreshRolePrivileges(role.get(),privilegeList);
            roleCache.refreshRoleRequestMatcher(role.get());

            return OP_STATUS_SUCCESS;
        }catch (Exception e){
            log.error("[EXCEPTION] updateRolePrivileges: {},{},{}",e.getMessage(),e.getCause(),e);
            return OP_STATUS_FAILED;
        }
    }


    @Override
    @Transactional
    public int addPrivilegesToRole(RoleUpdatePrivilege request){
        try {
            Optional<Role> role = roleRepository.findById(request.roleId());
            if (role.isEmpty()) {
                return OP_STATUS_ROLE_NOT_FOUND;
            }

            List<Privilege> privilegeList = checkPrivilege(request.privileges());
            if(privilegeList == null || privilegeList.size() < 1) return OP_STATUS_SUCCESS;

            role.get().getPrivileges().addAll(privilegeList);

            //REFRESH ROLES
            roleCache.refreshRolePrivileges(role.get(),List.copyOf(role.get().getPrivileges()));
            roleCache.refreshRoleRequestMatcher(role.get());

            return OP_STATUS_SUCCESS;
        }catch (Exception e){
            log.error("[EXCEPTION] addPrivilegesToRole: {},{},{}",e.getMessage(),e.getCause(),e);
            return OP_STATUS_FAILED;
        }
    }

    @Override
    public OpWrapper<List<RoleDto>> getAllRoles(){
        try{
            List<RoleDto> roles = roleCache.getDefinedRoles();
            return new OpWrapper<>(OP_STATUS_SUCCESS,roles);
        }catch (Exception e){
            log.error("[EXCEPTION] getAllPrivileges: {},{},{}",e.getMessage(),e.getCause(),e);
            return new OpWrapper<>(OP_STATUS_FAILED,null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OpWrapper<List<Privilege>> getAllPrivileges(){
        try{
            List<Privilege> privileges =  privilegeRepository.findAll();
            return new OpWrapper<>(OP_STATUS_SUCCESS,privileges);
        }catch (Exception e){
            log.error("[EXCEPTION] getAllPrivileges: {},{},{}",e.getMessage(),e.getCause(),e);
            return new OpWrapper<>(OP_STATUS_FAILED,null);
        }
    }

    @Override
    public OpWrapper<List<Privilege>> getRolePrivileges(String roleName){
        try{
            if(!roleCache.containsRole(roleName)){
                return new OpWrapper<>(OP_STATUS_ROLE_NOT_FOUND,null);
            }
            var privilegesList = roleCache.getRolePrivilege(roleName);
            List<Privilege> privileges = new ArrayList<>();
            if(privilegesList != null){
                 privileges.addAll(privilegesList);
            }
            return new OpWrapper<>(OP_STATUS_SUCCESS,privileges);
        }catch (Exception e){
            log.error("[EXCEPTION] getRolePrivilege: {},{},{}",e.getMessage(),e.getCause(),e);
            return new OpWrapper<>(OP_STATUS_FAILED,null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OpWrapper<List<Role>> getPrivilegeRoles(Long privilegeId){
        try{
            Optional<Privilege> privilege = privilegeRepository.findById(privilegeId);
            if(privilege.isEmpty()){
                new OpWrapper<>(OP_STATUS_PRIVILEGE_NOT_FOUND,null);
            }
            return new OpWrapper<>(OP_STATUS_SUCCESS,new ArrayList<>(privilege.get().getRoles()));
        }catch (Exception e){
            log.error("[EXCEPTION] getRolePrivilege: {},{},{}",e.getMessage(),e.getCause(),e);
            return new OpWrapper<>(OP_STATUS_FAILED,null);
        }
    }

    @Override
    @Transactional
    public int updateUserRoles(UpdateUserRolesRequest request){
       var opUser =  userService.getById(request.userId());
       if(opUser.isEmpty()) return OP_STATUS_USER_NOT_FOUND;
       var checked = checkRolesAndRemoveInvalid(request.roles());
       if(checked.status() != OP_STATUS_SUCCESS) return checked.status();
       opUser.get().setRoles(checked.data());
       return OP_STATUS_SUCCESS;
    }

    @Override
    @Transactional(readOnly = true)
    public OpWrapper<List<RoleDto>> getUserRoles(String userId){
        var user = userService.getById(userId);
        if(user.isEmpty()) return new OpWrapper<>(OP_STATUS_USER_NOT_FOUND,null);
        var roleDtoList = user.get().getRoles().stream().map(r -> new RoleDto(r.getId(),r.getName())).toList();
        return new OpWrapper<>(OP_STATUS_SUCCESS,roleDtoList);
    }

    @Override
    @Transactional
    public int updateRolePermissions(String roleName, Set<PermissionsEnum> permissions){
        var roleId =  roleCache.getRoleId(roleName);
        if(roleId.isEmpty()) return OP_STATUS_ROLE_NOT_FOUND;

        var role = roleRepository.findById(roleId.get());
        if(role.isEmpty()) return OP_STATUS_ROLE_NOT_FOUND;
        role.get().setPermissions(new ArrayList<>(permissions.stream().map(Enum::name).toList()));
        roleCache.updateRolePermissions(roleName,permissions);
        return OP_STATUS_SUCCESS;
    }

    @Override
    public OpWrapper<RolePermissionsDto> getRolePermissions(String roleName){
        var id  = roleCache.getRoleId(roleName);
        if(id.isEmpty()) return  new OpWrapper<>(OP_STATUS_ROLE_NOT_FOUND,null);
        var permissions = roleCache.getRolePermissions(roleName);
        return new OpWrapper<>(OP_STATUS_SUCCESS,new RolePermissionsDto(id.get(),roleName,permissions));
    }

    @Override
    public OpWrapper<List<String>> getAllPermissions(){
        var permissions = Arrays.stream(PermissionsEnum.values()).map(v -> v.name()).toList();
        return new OpWrapper<>(OP_STATUS_SUCCESS,permissions);
    }

    private OpWrapper<Set<Role>> checkRolesAndRemoveInvalid(Set<Long> roles) {
        Set<Role> checked = new HashSet<>();

        for (Long roleId : roles) {
            var role = roleRepository.findById(roleId);
            if (role.isEmpty()) return new OpWrapper<>(OP_STATUS_ROLE_NOT_FOUND, null);
            checked.add(role.get());
        }
        return new OpWrapper<>(OP_STATUS_SUCCESS, checked);
    }

    private List<Privilege> checkPrivilege(Set<Long> privilegesIds){
        List<Privilege> privilegeList = new ArrayList<>();
        for (Long privilegeId : privilegesIds) {
            Optional<Privilege> privilege = privilegeRepository.findById(privilegeId);
            if (privilege.isEmpty()) {
                return null;
            }
            privilegeList.add(privilege.get());
        }
        return privilegeList;
    }

}
