package common.management.common.config;

import common.management.common.model.Privilege;
import common.management.common.model.PrivilegeRepository;
import common.management.common.model.Role;
import common.management.common.payload.request.SignupRequest;
import common.management.common.repository.RoleRepository;
import common.management.common.repository.UserRepository;
import common.management.common.security.RoleCache;
import common.management.common.service.UserService;
import common.management.common.util.RoleType;
import common.management.staff.repository.StaffRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

import static common.management.common.util.GenerateRandom.generatePassword;
import static common.management.common.util.OperationStatus.OP_STATUS_SUCCESS;
import static common.management.common.util.OperationStatus.OP_STATUS_USERNAME_TAKEN;

@Configuration
@Slf4j
public class RolesConfig {
    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleCache roleCache;

    @Autowired
    private UserService userService;

    @Autowired private UserRepository userRepository;
    /*
	EXTRACT ALL ENDPOINTS AND SAVE TO DATABASE AS PRIVILEGES
	 */
    @EventListener
    @Order(1)
    public void initializePrivileges(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
                .getHandlerMethods();

        String start = "\\{";
        String end = "\\}";
        log.info(">>>>>> initialize privileges");
        HashMap<String,String> foundMethods = new HashMap(); // will be used to check for duplicated controller methods names

        for (RequestMappingInfo key : map.keySet()) {
            if (key.getPathPatternsCondition() == null) continue;
            String str = key.getPathPatternsCondition().getPatterns().toString();
            String uri = str.substring(1, str.length() - 1);
            var beanName = map.get(key).getBean().toString();
            var moduleName = beanName.replace("Controller", "");

//            String parsedUri = uri.replaceFirst(start + ".*" + end, "*");
            String parsedUri = replacePathVariableNameWithStar(uri);
            String method = key.getMethodsCondition().toString();
            String parsedMethod = method.substring(1, method.length() - 1);
            if (parsedMethod == null || parsedMethod.equals("")) continue;

            String name = map.get(key).getMethod().getName();
            if(foundMethods.containsKey(name)){
                log.warn("[initializePrivileges] duplicated controllers methods name: {{}} controller1: {{}} controller2: {{}}", name, beanName, foundMethods.get(name));
                continue;
            }
            foundMethods.put(name,beanName);

            Privilege privilege = privilegeRepository.findFirstByName(name);
            if (privilege == null) privilege = new Privilege();
            privilege.setName(name);
            privilege.setHttpMethod(parsedMethod);
            privilege.setUri(parsedUri);
            privilege.setModule(moduleName);
            privilegeRepository.save(privilege);
        }
    }

    @EventListener
    @Order(2)
    @Transactional
    public void createAdminUserAndRoles(ContextRefreshedEvent event) {

        Optional<Role> roleAdmin = roleRepository.findByName(RoleType.ROLE_ADMIN.name());
        Optional<Role> roleUser = roleRepository.findByName(RoleType.ROLE_USER.name());
        Optional<Role> roleEmp = roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name());
        if (roleAdmin.isEmpty()) {
            Role roleAdmin_ = new Role();
            roleAdmin_.setName(RoleType.ROLE_ADMIN.name());
            roleRepository.save(roleAdmin_);
        }
        if (roleUser.isEmpty()) {
            Role roleUser_ = new Role();
            roleUser_.setName(RoleType.ROLE_USER.name());
            roleRepository.save(roleUser_);
        }
        if(roleEmp.isEmpty()){
            Role roleEmp_ = new Role();
            roleEmp_.setName(RoleType.ROLE_EMPLOYEE.name());
            roleRepository.save(roleEmp_);
        }

        var oldAdmin = userRepository.findByUsername("admin");
        if(oldAdmin.isEmpty()){
            generateNewAdmin();
        }

        //init role cache
        roleCache.initRoleCache();
    }

    private void generateNewAdmin() {
        //generate default admin
        var pass = generatePassword(16);
        log.info("####### ADMIN PASSWORD =>> {}", pass);
        var user = userService.createNewUser(new SignupRequest("admin", pass, Set.of(RoleType.ROLE_ADMIN.name())));
        if(user.status() == OP_STATUS_USERNAME_TAKEN) return;
        if(user.status() != OP_STATUS_SUCCESS) throw new RuntimeException("Cannot create default admin");
    }

    private String replacePathVariableNameWithStar(String uri){
        StringBuilder sb = new StringBuilder();
        boolean bracketOpen = false;
        for (int i = 0; i < uri.length(); i++) {
            if(uri.charAt(i) == '{'){
                bracketOpen = true;
            }else if (uri.charAt(i) == '}'){
                bracketOpen = false;
                sb.append('*');
                continue; //skip this } char
            }

            if(!bracketOpen) sb.append(uri.charAt(i));
        }
        return sb.toString();
    }

}
