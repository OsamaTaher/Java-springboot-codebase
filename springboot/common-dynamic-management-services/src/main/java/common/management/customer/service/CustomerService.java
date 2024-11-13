package common.management.customer.service;

import common.management.common.util.OpWrapper;
import common.management.customer.payload.request.CreateCustomerRequest;
import common.management.customer.payload.response.CustomerDetailsResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    OpWrapper<Long> create(CreateCustomerRequest request);

    OpWrapper<CustomerDetailsResponse> getMyProfile();

    int updateProfilePicture(MultipartFile file);

}
