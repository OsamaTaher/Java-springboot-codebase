package common.management.administration.model;

import common.management.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLRestriction("deleted=false")
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    @NotBlank
    private String userId;

    public Admin create(String name,String userId){
        this.name = name;
        this.userId = userId;
        return this;
    }
}
