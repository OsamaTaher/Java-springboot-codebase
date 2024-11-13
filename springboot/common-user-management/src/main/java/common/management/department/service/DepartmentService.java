package common.management.department.service;

import common.management.common.util.OpWrapper;
import common.management.department.model.Department;
import common.management.department.payload.response.DepartmentDropdownResponse;

import java.util.List;

public interface DepartmentService {
    OpWrapper<List<DepartmentDropdownResponse>> getDropdown();
    OpWrapper<Department> getById(Long depId);
}
