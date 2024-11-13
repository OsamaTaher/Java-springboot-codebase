package common.management.department.model;

import common.management.common.model.Localize;
import common.management.department.payload.request.CreateDepartmentRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "departments")
@NoArgsConstructor
@Getter
@SQLRestriction("deleted=false")
public class Department extends Localize<CreateDepartmentRequest> {

    public Department create(CreateDepartmentRequest request){
        setNames(request);
        return this;
    }

}
