package common.management.common.security;

import common.management.administration.payload.response.RoleDto;
import common.management.common.model.Privilege;
import common.management.common.model.Role;
import common.management.common.repository.RoleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@Log4j2
public class ConcurrentHashMapRoleCache implements RoleCache {
    private final RoleRepository roleRepository;
    private ConcurrentMap<String, Role> definedRoles;
    private ConcurrentHashMap<String, List<RequestMatcher>> requestMatchers;

    @Autowired
    public ConcurrentHashMapRoleCache(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void initRoleCache() {
        log.info(">>>>>> initialize role cache");

        setDefinedRoles(new ConcurrentHashMap<>());
        setRequestMatchers(new ConcurrentHashMap<>());

        List<Role> roles = roleRepository.findAll();
        //INIT ROLES
        setDefinedRoles(
                roles.stream().collect(Collectors.toConcurrentMap(Role::getName, Function.identity())));

        //INIT ANTMATCHERS
        for (Role role : roles) {
            for (Privilege privilege : role.getPrivileges()) {
                if (privilege != null && privilege.getUri() != null && !privilege.getUri().equals("")) {
                    RequestMatcher requestMatcher = new AntPathRequestMatcher(privilege.getUri(), privilege.getHttpMethod());
                    if (!requestMatchers.containsKey(role.getName())) {
                        List<RequestMatcher> rlist = new ArrayList<>();
                        rlist.add(requestMatcher);
                        requestMatchers.put(role.getName(), rlist);
                    } else {
                        requestMatchers.get(role.getName()).add(requestMatcher);
                    }
                }
            }
        }
    }


    @Override
    public void refreshRoleRequestMatcher(Role role) {

        requestMatchers.put(role.getName(), new ArrayList<>());

        if (role.getPrivileges() == null) return;

        for (Privilege privilege : role.getPrivileges()) {
            if (privilege != null && privilege.getUri() != null && !privilege.getUri().equals("")) {
                RequestMatcher requestMatcher = new AntPathRequestMatcher(privilege.getUri(), privilege.getHttpMethod());
                if (!requestMatchers.containsKey(role.getName())) {
                    List<RequestMatcher> rlist = new ArrayList<>();
                    rlist.add(requestMatcher);
                    requestMatchers.put(role.getName(), rlist);
                } else {
                    requestMatchers.get(role.getName()).add(requestMatcher);
                }
            }
        }
    }

    @Override
    public void insertRole(Role role) {
        definedRoles.put(role.getName(), role);
        refreshRoleRequestMatcher(role);
    }

    @Override
    public void refreshRolePrivileges(Role role, List<Privilege> privileges) {
        definedRoles.get(role.getName()).setPrivileges(privileges);
    }

    @Override
    public List<RoleDto> getDefinedRoles() {
        return definedRoles.values().stream().map(r -> new RoleDto(r.getId(), r.getName())).toList();
    }

    @Override
    public Optional<Long> getRoleId(String roleName) {
        if (definedRoles.containsKey(roleName)) {
            return Optional.of(definedRoles.get(roleName).getId());
        }
        return Optional.empty();
    }

    @Override
    public List<Privilege> getRolePrivilege(String roleName) {
        if (definedRoles.containsKey(roleName)) {
            return Collections.unmodifiableList(definedRoles.get(roleName).getPrivileges().stream().toList());
        }
        return List.of();
    }

    private void setDefinedRoles(ConcurrentMap<String, Role> definedRoles) {
        this.definedRoles = definedRoles;
    }

    @Override
    public Map<String, List<RequestMatcher>> getRequestMatchers() {
        return Collections.unmodifiableMap(requestMatchers);
    }

    @Override
    public boolean containsRole(String roleName) {
        return definedRoles.containsKey(roleName);
    }

    private void setRequestMatchers(ConcurrentHashMap<String, List<RequestMatcher>> requestMatchers) {
        this.requestMatchers = requestMatchers;
    }
}
