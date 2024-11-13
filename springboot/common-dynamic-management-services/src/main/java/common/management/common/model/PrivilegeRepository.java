package common.management.common.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege,Long> {
    Privilege findFirstByName(String name);

    List<Privilege> findByModule(String moduleName);

    List<Privilege> findByNameIn(List<String> names);
}
