package common.management.administration.controller;

import common.management.administration.service.AdminUserServiceHandler;
import common.management.common.payload.request.ChangePasswordFromAdminRequest;
import common.management.common.util.OperationStatus;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/administration/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class AdministrationUserController {
    private final AdminUserServiceHandler serviceHandler;
    private final OperationStatus operationStatus;

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<?> changeUserPasswordByAdmin(@PathVariable(name = "userId") String userId,@RequestBody ChangePasswordFromAdminRequest request){
        return operationStatus.handle( serviceHandler.changePassword(userId, request));
    }
}
