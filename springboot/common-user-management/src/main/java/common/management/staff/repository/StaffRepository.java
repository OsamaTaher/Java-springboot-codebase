package common.management.staff.repository;

import common.management.staff.model.Staff;
import common.management.staff.payload.response.StaffDetailsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StaffRepository extends JpaRepository<Staff,Long> {
    Optional<Staff> findByUserId(String userId);


    @Query(value = """
            SELECT st.id AS id, st.user_id AS userId, st.first_name AS firstName,
            st.last_name AS lastName,
            u.username AS phone,
            u.enabled AS enabled
            FROM staff st
            LEFT JOIN users u ON u.id = st.user_id
            WHERE st.user_id = :userId
            """,nativeQuery = true)
    Optional<StaffDetailsResponse> findDetailsByUserId(String userId);

}
