package common.management.department.service.impl;

import common.management.administration.service.AdminDepartmentService;
import common.management.common.util.OpWrapper;
import common.management.department.model.Department;
import common.management.department.payload.request.CreateDepartmentRequest;
import common.management.department.payload.response.DepartmentDropdownResponse;
import common.management.department.repository.DepartmentRepository;
import common.management.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static common.management.common.util.OperationStatus.OP_STATUS_NOT_FOUND;
import static common.management.common.util.OperationStatus.OP_STATUS_SUCCESS;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService,AdminDepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public OpWrapper<Long> create(CreateDepartmentRequest request){
        var dep = new Department().create(request);
        var saved = departmentRepository.save(dep);
        return new OpWrapper<>(OP_STATUS_SUCCESS,saved.getId());
    }

    @Override
    public OpWrapper<List<DepartmentDropdownResponse>> getDropdown(){
        return new OpWrapper<>(OP_STATUS_SUCCESS,departmentRepository.findAllDropdown());
    }

    @Override
    public OpWrapper<Department> getById(Long depId){
        var dep = departmentRepository.findById(depId);
        if(dep.isEmpty()) return new OpWrapper<>(OP_STATUS_NOT_FOUND,null);
        return new OpWrapper<>(OP_STATUS_SUCCESS,dep.get());
    }

}
