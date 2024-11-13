package common.management.department.repository;

import common.management.department.model.Department;
import common.management.department.payload.response.DepartmentDropdownResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Long> {

    @Query("""
            SELECT d.id AS id, d.name AS name, d.nameAr AS nameAr, d.nameEx AS nameEx
            FROM Department d
            """)
    List<DepartmentDropdownResponse> findAllDropdown();
}
