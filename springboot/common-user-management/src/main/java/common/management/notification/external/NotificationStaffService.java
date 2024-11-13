package common.management.notification.external;

import common.management.staff.model.Staff;

import java.util.Optional;

public interface NotificationStaffService {
    Optional<Staff> getStaffMemberByUserId(String userId);

}
