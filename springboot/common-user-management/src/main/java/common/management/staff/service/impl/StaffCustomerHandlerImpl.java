package common.management.staff.service.impl;

import common.management.common.util.OpWrapper;
import common.management.customer.payload.response.CustomerDetailsResponse;
import common.management.staff.service.StaffCustomerHandler;
import common.management.staff.service.StaffCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffCustomerHandlerImpl implements StaffCustomerHandler {
    private final StaffCustomerService staffCustomerService;

    @Override
    public OpWrapper<CustomerDetailsResponse> getCustomerByUserId(String userId) {
        return staffCustomerService.getByUserId(userId);
    }
}
