package common.management.staff.model;

import common.management.common.model.BaseEntity;
import common.management.staff.payload.request.CreateStaffRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "staff")
@NoArgsConstructor
@Getter
@SQLRestriction("deleted=false")
public class Staff extends BaseEntity {
    @Column(nullable = false,unique = true)
    @NotBlank
    private String userId;
    @Column(nullable = false)
    @NotBlank
    private String firstName;
    @Column(nullable = false)
    @NotBlank
    private String lastName;


    public Staff create(CreateStaffRequest request,String userId){
        firstName = request.firstName();
        lastName  = request.lastName();
        this.userId = userId;
        return this;
    }
}
