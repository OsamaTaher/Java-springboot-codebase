package common.management.common.payload.response;

public record BasicResponse(
        int responseCode,
        String message
) {
}
