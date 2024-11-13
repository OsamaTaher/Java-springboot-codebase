package common.management.common.controller;

import common.management.common.payload.request.ChangePasswordRequest;
import common.management.common.payload.request.LoginRequest;
import common.management.common.payload.request.RefreshRequest;
import common.management.common.service.UserService;
import common.management.common.util.OperationStatus;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static common.management.common.util.OperationStatus.OP_STATUS_SUCCESS;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
public class UserController {
    @Autowired
    private OperationStatus statusHandler;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest request){
        var op  = userService.login(request);
        if(op.status() != OP_STATUS_SUCCESS){
           return statusHandler.handle(op.status());
        }
        return ResponseEntity.ok().body(op.data());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshMyToken(@RequestBody RefreshRequest request){
        var op = userService.getNewToken(request);
        if(op.status()!=OP_STATUS_SUCCESS){
            return statusHandler.handle(op.status());
        }
        return ResponseEntity.ok(op.data());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request){
        return statusHandler.handle( userService.changePassword(request));
    }

    @GetMapping("/enable/{userId}")
    public ResponseEntity<?> enableUser(@PathVariable(name = "userId")String userId){
        return statusHandler.handle(userService.enableUser(userId));
    }

    @GetMapping("/disable/{userId}")
    public ResponseEntity<?> disableUser(@PathVariable(name = "userId")String userId){
        return statusHandler.handle(userService.disableUser(userId));
    }

    @GetMapping("/my-privileges")
    public ResponseEntity<?> getMyPrivileges() {
        return statusHandler.handle(userService.getMyPrivileges());
    }
}
