package common.management.common.repository;

import common.management.common.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths ={"roles"})
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}
