package common.management.department.controller;

import common.management.common.util.OperationStatus;
import common.management.department.service.DepartmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final OperationStatus operationStatus;

    @GetMapping("/dropdown")
    public ResponseEntity<?> getDepartmentDropdown(){
        return operationStatus.handle(departmentService.getDropdown());
    }

}
