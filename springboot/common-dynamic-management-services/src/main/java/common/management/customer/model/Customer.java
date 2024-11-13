package common.management.customer.model;

import common.management.common.model.BaseEntity;
import common.management.customer.payload.request.CreateCustomerRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@Getter
@SQLRestriction("deleted=false")
public class Customer extends BaseEntity {
    @Column(nullable = false,unique = true)
    @NotBlank
    private String userId;
    @Column(nullable = false)
    @NotBlank
    private String firstName;
    @Column(nullable = false)
    @NotBlank
    private String lastName;

    private String profilePicture;

    public Customer create(CreateCustomerRequest request, String userId){
        firstName = request.firstName();
        lastName  = request.lastName();
        this.userId = userId;
        return this;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
