package common.management.administration.service;

import common.management.common.payload.request.ChangePasswordFromAdminRequest;
import org.springframework.transaction.annotation.Transactional;

public interface AdminUserService {
    @Transactional
    int changePasswordAdmin(String userId,ChangePasswordFromAdminRequest request);
}
