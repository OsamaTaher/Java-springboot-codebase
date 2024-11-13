package common.management.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface AccessChecker {

    boolean check(Authentication authentication, HttpServletRequest request);

    boolean roleHasPermission(String roleName, PermissionsEnum permission);
}
