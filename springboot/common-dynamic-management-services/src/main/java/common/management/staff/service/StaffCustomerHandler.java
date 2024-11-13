package common.management.staff.service;

import common.management.common.util.OpWrapper;
import common.management.customer.payload.response.CustomerDetailsResponse;

public interface StaffCustomerHandler {
    OpWrapper<CustomerDetailsResponse> getCustomerByUserId(String userId);
}
