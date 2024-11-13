package common.management.staff.service.impl;

import common.management.administration.payload.request.CreateStaffRequest;
import common.management.administration.service.AdministrateStaffService;
import common.management.common.payload.request.SignupRequest;
import common.management.common.service.UserService;
import common.management.common.util.OpWrapper;
import common.management.common.util.RoleType;
import common.management.notification.external.NotificationStaffService;
import common.management.staff.model.Staff;
import common.management.staff.payload.response.StaffDetailsResponse;
import common.management.staff.repository.StaffRepository;
import common.management.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.Set;

import static common.management.common.util.OperationStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService, AdministrateStaffService, NotificationStaffService {
    private final StaffRepository staffRepository;
    private final UserService userService;

    @Override
    public OpWrapper<Staff> create(common.management.staff.payload.request.CreateStaffRequest request) {
        var user = userService.createNewUser(new SignupRequest(request.phone(), request.password(), Set.of(request.role())));
        if (user.status() != OP_STATUS_SUCCESS) return new OpWrapper<>(user.status(), null);
        var staff = new Staff().create(request, user.data().getId());
        var saved = staffRepository.save(staff);
        return new OpWrapper<>(OP_STATUS_SUCCESS, saved);
    }

    @Override
    @Transactional
    public OpWrapper<Long> createEmployee(CreateStaffRequest request) {
        return createStaff(request, RoleType.ROLE_EMPLOYEE.name());
    }

    private OpWrapper<Long> createStaff(CreateStaffRequest request, String role) {
        var staff = create(request.toCreateStaffRequest(role));
        if (staff.status() != OP_STATUS_SUCCESS) return new OpWrapper<>(staff.status(), null);
        return new OpWrapper<>(OP_STATUS_SUCCESS, staff.data().getId());
    }

    @Override
    public Optional<Staff> getStaffMemberByUserId(String userId){
        return staffRepository.findByUserId(userId);
    }

    @Override
    public OpWrapper<StaffDetailsResponse> getDetailsByUserId(String userId){
        var staff = staffRepository.findDetailsByUserId(userId);
        if(staff.isEmpty()) return new OpWrapper<>(OP_STATUS_NOT_FOUND,null);
        return new OpWrapper<>(OP_STATUS_SUCCESS,staff.get());
    }
}
