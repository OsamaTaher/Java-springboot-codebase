package common.management.staff.service;

import common.management.common.util.OpWrapper;
import common.management.customer.payload.response.CustomerDetailsResponse;

public interface StaffCustomerService {
    OpWrapper<CustomerDetailsResponse> getByUserId(String userId);
}
