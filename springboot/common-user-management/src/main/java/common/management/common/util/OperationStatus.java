package common.management.common.util;

import common.management.common.payload.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OperationStatus {
    @Autowired
    private MessageSource messageSource;

    public static final int OP_STATUS_SUCCESS = 0;
    public static final int OP_STATUS_SUCCESS_CREATED = 1;
    public static final int OP_STATUS_FAILED = -1;
    public static final int OP_STATUS_FORBIDDEN = -2;
    public static final int OP_STATUS_BAD_REQUEST = -3;

    //AUTH ERRORS
    public static final int OP_STATUS_USER_ACCOUNT_LOCKED = -4;
    public static final int OP_STATUS_BAD_CREDENTIAL = -5;
    public static final int OP_STATUS_USERNAME_TAKEN = -6;
    public static final int OP_STATUS_ROLE_NOT_FOUND = -7;
    //
    public static final int OP_STATUS_PASSWORD_MISMATCH = -8;
    public static final int OP_STATUS_ROLE_EXIST = -9;
    public static final int OP_STATUS_USER_NOT_FOUND = -10;

    //File
    public static final int OP_STATUS_INVALID_FILE_NAME = -11;
    public static final int OP_STATUS_FILE_EMPTY = -12;
    public static final int OP_STATUS_FILE_DATA_NOT_FOUND = -13;
    public static final int OP_STATUS_FILETYPE_MISMATCH = -14;
    public static final int OP_STATUS_WRONG_PASSWORD = -15;
    public static final int OP_STATUS_REFRESH_TOKEN_INVALID = -16;
    public static final int OP_STATUS_FILE_TYPE_NOT_ALLOWED= -36;

    public static final int OP_STATUS_EMAIL_ALREADY_USED_BY_ANOTHER_ACCOUNT = -17;
    public static final int OP_STATUS_PRIVILEGE_NOT_FOUND = -18;
    public static final int OP_STATUS_INVALID_SECRET = -19;
    public static final int OP_STATUS_EXPIRED_SECRET = -20;
    public static final int OP_STATUS_REGION_WITH_SAME_NAME_EXISTS = -23;
    public static final int OP_STATUS_REGION_NOT_FOUND = -24;
    public static final int OP_STATUS_GOVERNORATE_NOT_FOUND = -26;
    public static final int OP_STATUS_STAFF_MEMBER_NOT_FOUND = -25;
    public static final int OP_STATUS_COMPLAINT_NOT_FOUND = -31;
    public static final int OP_STATUS_CUSTOMER_NOT_FOUND = -32;
    public static final int OP_STATUS_NOT_FOUND = -35;
    //CUSTOM STATUS CODE
    public static final int CUSTOM_HTTP_STATUS_VALIDATION_ERROR = 600;
    public static final int RESPONSE_CODE_REFRESH_TOKEN_INVALID = 611;


    public ResponseEntity<?> handle(OpWrapper<?> opWrapper) {
        if (opWrapper.status() != OP_STATUS_SUCCESS) {
            return handle(opWrapper.status());
        }
        return ResponseEntity.ok().body(opWrapper.data());
    }

    public ResponseEntity<?> handle(int status) {

        switch (status) {
            case OP_STATUS_SUCCESS:
                return ResponseEntity.ok().body(new BasicResponse(HttpStatus.OK.value(), messageSource.getMessage("OP_STATUS_SUCCESS", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_SUCCESS_CREATED:
                return ResponseEntity.ok().body(new BasicResponse(HttpStatus.CREATED.value(), messageSource.getMessage("OP_STATUS_SUCCESS_CREATED", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_USER_ACCOUNT_LOCKED:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BasicResponse(HttpStatus.FORBIDDEN.value(), messageSource.getMessage("OP_STATUS_USER_ACCOUNT_LOCKED", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_BAD_CREDENTIAL:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BasicResponse(HttpStatus.UNAUTHORIZED.value(), messageSource.getMessage("OP_STATUS_BAD_CREDENTIAL", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_ROLE_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(HttpStatus.BAD_REQUEST.value(), messageSource.getMessage("OP_STATUS_ROLE_NOT_FOUND", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_USERNAME_TAKEN:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new BasicResponse(HttpStatus.CONFLICT.value(), messageSource.getMessage("OP_STATUS_USERNAME_TAKEN", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_FORBIDDEN:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BasicResponse(HttpStatus.FORBIDDEN.value(), messageSource.getMessage("OP_STATUS_FORBIDDEN", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_PASSWORD_MISMATCH:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(HttpStatus.BAD_REQUEST.value(), messageSource.getMessage("OP_STATUS_PASSWORD_MISMATCH", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_ROLE_EXIST:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(HttpStatus.BAD_REQUEST.value(), messageSource.getMessage("OP_STATUS_ROLE_EXIST", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_USER_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BasicResponse(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("OP_STATUS_USER_NOT_FOUND", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_PRIVILEGE_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BasicResponse(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("OP_STATUS_PRIVILEGE_NOT_FOUND", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_WRONG_PASSWORD:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BasicResponse(HttpStatus.FORBIDDEN.value(), messageSource.getMessage("OP_STATUS_WRONG_PASSWORD", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_REFRESH_TOKEN_INVALID:
                return ResponseEntity.status(RESPONSE_CODE_REFRESH_TOKEN_INVALID).body(new BasicResponse(RESPONSE_CODE_REFRESH_TOKEN_INVALID, messageSource.getMessage("OP_STATUS_REFRESH_TOKEN_INVALID", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_EMAIL_ALREADY_USED_BY_ANOTHER_ACCOUNT:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(HttpStatus.BAD_REQUEST.value(), messageSource.getMessage("OP_STATUS_EMAIL_ALREADY_USED_BY_ANOTHER_ACCOUNT", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_INVALID_SECRET:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BasicResponse(HttpStatus.FORBIDDEN.value(), messageSource.getMessage("OP_STATUS_INVALID_SECRET", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_EXPIRED_SECRET:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BasicResponse(HttpStatus.FORBIDDEN.value(), messageSource.getMessage("OP_STATUS_EXPIRED_SECRET", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_REGION_WITH_SAME_NAME_EXISTS:
                return ResponseEntity.status(HttpStatus.FOUND).body(new BasicResponse(HttpStatus.FOUND.value(), messageSource.getMessage("OP_STATUS_REGION_WITH_SAME_NAME_EXISTS", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_REGION_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.FOUND).body(new BasicResponse(HttpStatus.FOUND.value(), messageSource.getMessage("OP_STATUS_REGION_NOT_FOUND", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_STAFF_MEMBER_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BasicResponse(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("OP_STATUS_STAFF_MEMBER_NOT_FOUND", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_BAD_REQUEST:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(HttpStatus.BAD_REQUEST.value(), messageSource.getMessage("OP_STATUS_BAD_REQUEST", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_COMPLAINT_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BasicResponse(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("OP_STATUS_COMPLAINT_NOT_FOUND", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_CUSTOMER_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BasicResponse(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("OP_STATUS_CUSTOMER_NOT_FOUND", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BasicResponse(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("OP_STATUS_NOT_FOUND", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_FILE_EMPTY:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(HttpStatus.BAD_REQUEST.value(), messageSource.getMessage("OP_STATUS_FILE_EMPTY", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_INVALID_FILE_NAME:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(HttpStatus.BAD_REQUEST.value(), messageSource.getMessage("OP_STATUS_INVALID_FILE_NAME", null, LocaleContextHolder.getLocale())));
            case OP_STATUS_FILE_TYPE_NOT_ALLOWED:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BasicResponse(HttpStatus.FORBIDDEN.value(), messageSource.getMessage("OP_STATUS_FILE_TYPE_NOT_ALLOWED", null, LocaleContextHolder.getLocale())));

            default:
                return ResponseEntity.internalServerError().body(new BasicResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Operation Failed"));
        }
    }

}
