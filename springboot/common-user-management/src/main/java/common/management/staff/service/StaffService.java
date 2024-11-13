package common.management.staff.service;

import common.management.administration.payload.request.CreateStaffRequest;
import common.management.common.util.OpWrapper;
import common.management.staff.payload.response.StaffDetailsResponse;

public interface StaffService {

    OpWrapper<Long> createEmployee(CreateStaffRequest request);

    OpWrapper<StaffDetailsResponse> getDetailsByUserId(String userId);
}

