package common.management.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class Localize<T extends BaseNames> extends BaseEntity{
    @Column(nullable = false)
    @NotBlank
    private String name;
    private String nameAr;
    private String nameEx;

    public void setNames(T request){
        this.setName(request.getName());
        this.setNameAr(request.getNameAr());
        this.setNameEx(request.getNameEx());
    }
}
