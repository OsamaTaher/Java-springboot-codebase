package common.management.administration.service;

import common.management.common.payload.request.ChangePasswordFromAdminRequest;

public interface AdminUserServiceHandler {
    int changePassword(String userId, ChangePasswordFromAdminRequest request);
}
