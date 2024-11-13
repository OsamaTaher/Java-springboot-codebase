package common.management.department.payload.request;

import common.management.common.model.BaseNames;
import jakarta.validation.constraints.NotBlank;

public record CreateDepartmentRequest(
        @NotBlank
        String name,
        String nameAr,
        String nameEx
) implements BaseNames {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNameAr() {
        return nameAr;
    }

    @Override
    public String getNameEx() {
        return nameEx;
    }
}
