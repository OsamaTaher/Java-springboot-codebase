package common.management.administration.service;

import common.management.common.util.OpWrapper;
import common.management.staff.model.Staff;
import common.management.staff.payload.request.CreateStaffRequest;

public interface AdministrateStaffService {
    OpWrapper<Staff> create(CreateStaffRequest request);
}
