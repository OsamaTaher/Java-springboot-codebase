package common.management.administration.service.impl;

import common.management.administration.service.AdminUserService;
import common.management.administration.service.AdminUserServiceHandler;
import common.management.common.payload.request.ChangePasswordFromAdminRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceHandlerImpl implements AdminUserServiceHandler {
    private final AdminUserService adminUserService;

    @Override
    public int changePassword(String userId,ChangePasswordFromAdminRequest request){
        return adminUserService.changePasswordAdmin(userId,request);
    }
}
