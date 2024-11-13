package common.management.common.service;

import common.management.common.model.Privilege;
import common.management.common.model.User;
import common.management.common.payload.request.*;
import common.management.common.security.PermissionsEnum;
import common.management.common.util.OpWrapper;

import java.util.List;
import java.util.Optional;

public interface UserService{
    OpWrapper<?> login(LoginRequest request);

    OpWrapper<?> getNewToken(RefreshRequest request);

    OpWrapper<User> createNewUser(SignupRequest request);

    int changePassword(ChangePasswordRequest request);

    int enableUser(String userId);

    int disableUser(String userId);

    Optional<User> getById(String userId);

    User getAuthenticatedUser();

    Optional<String> getAuthUserId();

    boolean isAdmin();

    boolean isManager();

    boolean hasPermission(PermissionsEnum permission);

    OpWrapper<List<Privilege>> getMyPrivileges();
}
