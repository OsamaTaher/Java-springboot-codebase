package common.management.customer.controller;

import common.management.common.util.OperationStatus;
import common.management.customer.payload.request.CreateCustomerRequest;
import common.management.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class CustomerController {
    private final CustomerService customerService;
    private final OperationStatus operationStatus;

    @PostMapping("/signup")
    public ResponseEntity<?> customerSignup(@Valid @RequestBody CreateCustomerRequest request){
        return operationStatus.handle(customerService.create(request));
    }

    @PutMapping(value = "/profile-picture",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfilePicture(@RequestPart MultipartFile file){
        return operationStatus.handle(customerService.updateProfilePicture(file));
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> getMyProfile(){
        return operationStatus.handle(customerService.getMyProfile());
    }

}
