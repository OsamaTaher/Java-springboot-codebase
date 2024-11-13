package common.management.common.service.impl;

import common.management.administration.service.AdminUserService;
import common.management.common.model.Privilege;
import common.management.common.model.Role;
import common.management.common.model.User;
import common.management.common.payload.request.ChangePasswordFromAdminRequest;
import common.management.common.payload.request.ChangePasswordRequest;
import common.management.common.payload.request.LoginRequest;
import common.management.common.payload.request.RefreshRequest;
import common.management.common.payload.request.SignupRequest;
import common.management.common.payload.response.LoginResponse;
import common.management.common.repository.RoleRepository;
import common.management.common.repository.UserRepository;
import common.management.common.security.AccessChecker;
import common.management.common.security.PermissionsEnum;
import common.management.common.security.RoleCache;
import common.management.common.service.JwtService;
import common.management.common.service.RefreshTokenService;
import common.management.common.service.UserService;
import common.management.common.util.OpWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static common.management.common.util.OperationStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, AdminUserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final AccessChecker accessChecker;
    private final RoleCache roleCache;

    @Override
    public OpWrapper<?> login(LoginRequest request) {
        try {
            //Authenticate user
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User userDetails = (User) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            String jwt = jwtService.generateToken(authentication);

            var refresh = refreshTokenService.createRefreshToken(userDetails.getId());
            if (refresh.status() != OP_STATUS_SUCCESS) return refresh;

            var response = new LoginResponse(jwt, "Bearer", userDetails.getId(), userDetails.getUsername(), roles, refresh.data());
            return new OpWrapper<>(OP_STATUS_SUCCESS, response);
        } catch (DisabledException | LockedException e) {
            return new OpWrapper<>(OP_STATUS_USER_ACCOUNT_LOCKED, null);
        } catch (BadCredentialsException e) {
            log.error("[BadCredentialsException] User doLogin: {},{}", e.getMessage(), e.getCause());
            return new OpWrapper<>(OP_STATUS_BAD_CREDENTIAL, null);
        } catch (Exception e) {
            log.error("[EXCEPTION] User doLogin: {}", e.getMessage(), e.getCause());
            return new OpWrapper<>(OP_STATUS_FAILED, null);
        }
    }

    @Override
    @Transactional
    public OpWrapper<?> getNewToken(RefreshRequest request) {
        try {
            var check = refreshTokenService.isRefreshTokenValid(request.refreshToken());
            if (check.status() == OP_STATUS_SUCCESS) {
                //generate new refresh token and Jwt
                List<String> authorities = check.data().getUser().getRoles().stream().map(Role::getName).collect(Collectors.toList());
                String jwt = jwtService.generateToken(check.data().getUser().getId(), authorities);
                var refresh = refreshTokenService.createRefreshToken(check.data().getUser().getId());
                var response = new LoginResponse(jwt, "Bearer", check.data().getUser().getId(), check.data().getUser().getUsername(), authorities, refresh.data());
                return new OpWrapper<>(OP_STATUS_SUCCESS, response);
            } else {
                return check;
            }
        } catch (Exception e) {
            return new OpWrapper<>(OP_STATUS_FAILED, null);
        }
    }

    @Override
    public OpWrapper<User> createNewUser(SignupRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return new OpWrapper<>(OP_STATUS_USERNAME_TAKEN, null);
        }
        User user = new User();
        var checked = setUserDetails(request, user);
        if (checked.status() != OP_STATUS_SUCCESS) return checked;

        var saved = userRepository.save(checked.data());
        return new OpWrapper<>(OP_STATUS_SUCCESS, saved);
    }

    public OpWrapper<Set<Role>> checkRoles(Set<String> roles) {
        Set<Role> checked = new HashSet<>();
        if (roles == null) {
            var userRole = roleRepository.findByName("ROLE_USER");
            if (userRole.isEmpty()) return new OpWrapper<>(OP_STATUS_ROLE_NOT_FOUND, null);
            checked.add(userRole.get());
        } else {
            for (String role : roles) {
                var other = roleRepository.findByName(role);
                if (other.isEmpty()) return new OpWrapper<>(OP_STATUS_ROLE_NOT_FOUND, null);
                checked.add(other.get());
            }
        }
        return new OpWrapper<>(OP_STATUS_SUCCESS, checked);
    }

    @Override
    @Transactional
    public int changePassword(ChangePasswordRequest request) {
        try {
            User user = getAuthenticatedUser();
            if (user == null) {
                return OP_STATUS_USER_NOT_FOUND;
            }

            if (encoder.matches(request.oldPassword(), user.getPassword())) {
                if (!request.newPassword().equals(request.confirmNewPassword())) {
                    return OP_STATUS_PASSWORD_MISMATCH;
                }
                User u = updateUserPassword(request.newPassword(), user);
                if (encoder.matches(request.newPassword(), u.getPassword())) {
                    return OP_STATUS_SUCCESS;
                }
                return OP_STATUS_FAILED;
            }
            return OP_STATUS_WRONG_PASSWORD;
        } catch (Exception e) {
            log.error("[EXCEPTION] changePassword:{},{},{}", e.getMessage(), e.getCause(), e);
            return OP_STATUS_FAILED;
        }
    }

    @Override
    @Transactional
    public int changePasswordAdmin(String userId, ChangePasswordFromAdminRequest request) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return OP_STATUS_USER_NOT_FOUND;
            }
            if (!request.newPassword().equals(request.confirmNewPassword())) {
                return OP_STATUS_PASSWORD_MISMATCH;
            }
            User u = updateUserPassword(request.newPassword(), user.get());
            if (encoder.matches(request.newPassword(), u.getPassword())) {
                return OP_STATUS_SUCCESS;
            }
            return OP_STATUS_FAILED;
        } catch (Exception e) {
            log.error("[EXCEPTION] changePassword:{},{},{}", e.getMessage(), e.getCause(), e);
            return OP_STATUS_FAILED;
        }
    }

    @Override
    @Transactional
    public int enableUser(String userId) {
        try {
            var user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return OP_STATUS_USER_NOT_FOUND;
            }
            user.get().setEnabled(true);
            return OP_STATUS_SUCCESS;
        } catch (Exception e) {
            log.error("[EXCEPTION] enableUser: {}{}", e.getMessage(), e.getCause());
            return OP_STATUS_FAILED;
        }
    }

    @Override
    public int disableUser(String userId) {
        try {
            var user = userRepository.findById(userId);
            if (user.isEmpty()) return OP_STATUS_USER_NOT_FOUND;
            user.get().setEnabled(true);
            return OP_STATUS_SUCCESS;
        } catch (Exception e) {
            log.error("[EXCEPTION] disable user: {}{}", e.getMessage(), e.getCause());
            return OP_STATUS_FAILED;
        }
    }

    protected User updateUserPassword(String password, User u) {
        u.setPassword(encoder.encode(password));
        return u;
    }

    @Override
    public Optional<User> getById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User getAuthenticatedUser() {
        var userId = getAuthUserId();
        if (userId.isEmpty()) return null;

        return userRepository.findById(userId.get()).orElse(null);
    }

    @Override
    public Optional<String> getAuthUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object principal = auth.getPrincipal();
            if(principal.toString().equals("anonymousUser")) return Optional.empty();
            return Optional.of(principal.toString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isAdmin() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isManager() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_MANAGER"));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasPermission(PermissionsEnum permission) {
        try {
            if (isAdmin()) return true;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth.getAuthorities().stream().anyMatch(g -> accessChecker.roleHasPermission(g.getAuthority(), permission));
        } catch (Exception e) {
            log.error("[EXCEPTION] hasPermission :{},{}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public OpWrapper<List<Privilege>> getMyPrivileges() {
        HashMap<Long, Privilege> map = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (var grandAuth : auth.getAuthorities()) {
            var roleName = grandAuth.getAuthority();
            roleCache.getRolePrivilege(roleName).forEach(p -> map.putIfAbsent(p.getId(), p));
        }
        return new OpWrapper<>(OP_STATUS_SUCCESS, map.values().stream().toList());
    }

    protected OpWrapper<User> setUserDetails(SignupRequest request, User u) {
        u.setUsername(request.username());
        u.setPassword(encoder.encode(request.password()));
        var roles = checkRoles(request.roles());
        if (roles.status() != OP_STATUS_SUCCESS) return new OpWrapper<>(roles.status(), null);
        u.setRoles(roles.data());
        u.setEnabled(true);
        return new OpWrapper<>(OP_STATUS_SUCCESS, u);
    }
}
