package common.management.administration.service;

import common.management.common.util.OpWrapper;
import common.management.department.model.Department;
import common.management.department.payload.request.CreateDepartmentRequest;

public interface AdminDepartmentService {
    OpWrapper<Long> create(CreateDepartmentRequest request);
    OpWrapper<Department> getById(Long depId);
}
