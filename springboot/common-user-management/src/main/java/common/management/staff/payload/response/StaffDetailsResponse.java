package common.management.staff.payload.response;

public interface StaffDetailsResponse {
    Long getId();
    String getUserId();
    String getFirstName();
    String getLastName();
    String getPhone();
    Boolean getEnabled();
}
