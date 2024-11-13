package common.management.customer.repository;

import common.management.customer.model.Customer;
import common.management.customer.payload.response.CustomerDetailsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    @Query(value = """
            SELECT c.id AS id, c.first_name AS firstName, c.last_name AS lastName,
            c.profile_picture AS profilePicture,
            u.username AS phone,
            u.id AS userId
            FROM customers c
            LEFT JOIN users u ON u.id = c.user_id
            WHERE c.user_id = :userId
            """,nativeQuery = true)
    Optional<CustomerDetailsResponse> findByUserIdCustom(String userId);


    Optional<Customer> findByUserId(String userId);
}
