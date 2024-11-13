package common.management.common.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleChecker implements AccessChecker{
    private final RoleCache roleCache;

    @Override
    public boolean check(Authentication authentication, HttpServletRequest request) {
        if(authentication == null ||  !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken){
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasAccess = false;

        for(GrantedAuthority role:authorities){
            String name = role.toString();

            //Allow admin to access all APIs
            if(name.equals("ROLE_ADMIN")) return true;

            List<RequestMatcher> requestMatchers = roleCache.getRequestMatchers().get(name);
            if(requestMatchers == null) continue;
            for(RequestMatcher requestMatcher : requestMatchers){
                hasAccess = requestMatcher.matches(request);
                if(hasAccess) break;
            }
            if(hasAccess) break;
        }
        if(!hasAccess){
            log.warn("[Access Not Allowed] check => {}, {}", request.getMethod(),request.getRequestURI());
        }
        return hasAccess;
    }

    @Override
    public boolean roleHasPermission(String roleName, PermissionsEnum permission){
        return roleCache.getRolePermissions(roleName).contains(permission.name());
    }
}
