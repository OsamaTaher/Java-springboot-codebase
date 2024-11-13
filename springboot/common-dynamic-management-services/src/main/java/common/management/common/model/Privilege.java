package common.management.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity(name = "privileges")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Privilege extends BaseEntity{

    @Column(nullable = false,unique = true)
    private String name;

    private String displayNameEn;

    private String displayNameAr;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private String httpMethod;

    @Column(nullable = false)
    private String module;

    @ManyToMany(mappedBy = "privileges")
    @JsonIgnore
    private Collection<Role> roles;
}
