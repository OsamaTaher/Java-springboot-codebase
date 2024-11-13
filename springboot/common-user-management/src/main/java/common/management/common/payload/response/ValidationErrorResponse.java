package common.management.common.payload.response;

import java.util.List;

public record ValidationErrorResponse(Integer count, List<ValidationError> errors) {
}