package common.management.staff.controller;

import common.management.common.util.OperationStatus;
import common.management.staff.service.StaffService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class StaffController {
    private final StaffService staffService;
    private final OperationStatus operationStatus;


    @GetMapping("/{userId}")
    public ResponseEntity<?> getStaffByUserId(@PathVariable(name = "userId") String userId ){
        return operationStatus.handle(staffService.getDetailsByUserId(userId));
    }

}
