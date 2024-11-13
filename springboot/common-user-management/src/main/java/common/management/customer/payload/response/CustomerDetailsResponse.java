package common.management.customer.payload.response;


public interface CustomerDetailsResponse {
    Long getId();

    String getUserId();

    String getFirstName();

    String getLastName();

    String getPhone();

    String getProfilePicture();
}
