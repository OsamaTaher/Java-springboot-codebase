package common.management.staff.controller;

import common.management.common.util.OperationStatus;
import common.management.staff.service.StaffCustomerHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/staff/customers")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class StaffCustomerController {
    private final StaffCustomerHandler staffCustomerHandler;
    private final OperationStatus operationStatus;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCustomerByUserId(@PathVariable(name = "userId") String userId ){
        return operationStatus.handle(staffCustomerHandler.getCustomerByUserId(userId));
    }
}
