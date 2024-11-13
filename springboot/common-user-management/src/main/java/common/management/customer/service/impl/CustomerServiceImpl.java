package common.management.customer.service.impl;

import common.management.common.payload.request.SignupRequest;
import common.management.common.service.StorageService;
import common.management.common.service.UserService;
import common.management.common.util.OpWrapper;
import common.management.common.util.RoleType;
import common.management.customer.model.Customer;
import common.management.customer.payload.request.CreateCustomerRequest;
import common.management.customer.payload.response.CustomerDetailsResponse;
import common.management.customer.repository.CustomerRepository;
import common.management.customer.service.*;
import common.management.customer.service.CustomerService;
import common.management.staff.service.StaffCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static common.management.common.util.OperationStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService, StaffCustomerService {
    private final UserService userService;
    private final CustomerRepository customerRepository;
    private final StorageService storageService;


    @Override
    @Transactional
    public OpWrapper<Long> create(CreateCustomerRequest request) {
        var user = userService.createNewUser(new SignupRequest(request.phone(), request.password(), Set.of(RoleType.ROLE_USER.name())));
        if (user.status() != OP_STATUS_SUCCESS) return new OpWrapper<>(user.status(),null);
        var customer = new Customer().create(request,user.data().getId());
        var saved = customerRepository.save(customer);
        return new OpWrapper<>(OP_STATUS_SUCCESS,saved.getId());
    }


    @Override
    @Transactional
    public int updateProfilePicture(MultipartFile file) {
        //make sure file is image
        if(!storageService.isImage(file)) return OP_STATUS_FILE_TYPE_NOT_ALLOWED;

        var userId = userService.getAuthUserId();
        if(userId.isEmpty()) return OP_STATUS_FORBIDDEN;
        var customer  = customerRepository.findByUserId(userId.get());
        if(customer.isEmpty())return OP_STATUS_CUSTOMER_NOT_FOUND;

        String fileNameAndExt = storageService.generateFullFileName("customer", file);
        int result = storageService.store(file, fileNameAndExt);
        if (result != OP_STATUS_SUCCESS) return result;

        customer.get().setProfilePicture(fileNameAndExt);
        return OP_STATUS_SUCCESS;
    }

    @Override
    public OpWrapper<CustomerDetailsResponse> getMyProfile() {
        var userId = userService.getAuthUserId();
        if(userId.isEmpty()) return new OpWrapper<>(OP_STATUS_CUSTOMER_NOT_FOUND,null);
        return getByUserId(userId.get());
    }

    @Override
    public OpWrapper<CustomerDetailsResponse> getByUserId(String userId){
        var customerDetails = customerRepository.findByUserIdCustom(userId);
        if(customerDetails.isEmpty()) return new OpWrapper<>(OP_STATUS_COMPLAINT_NOT_FOUND,null);
        return new OpWrapper<>(OP_STATUS_SUCCESS,customerDetails.get());
    }

}
