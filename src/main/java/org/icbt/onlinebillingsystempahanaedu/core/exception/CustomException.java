package org.icbt.onlinebillingsystempahanaedu.core.exception;

/**
 * author : Niwanthi
 * date : 8/9/2025
 * time : 10:37 PM
 */
public class CustomException extends RuntimeException{

    private final ExceptionType exceptionType;

    public CustomException(ExceptionType exceptionType) {
        super(exceptionType.name());
        this.exceptionType = exceptionType;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }
    public enum ExceptionType {
        // ===== CUSTOMER EXCEPTION TYPES =====
        CUSTOMER_NOT_FOUND,
        CUSTOMER_ALREADY_EXISTS,
        INVALID_CUSTOMER_INPUTS,
        CUSTOMER_ACCOUNT_NUMBER_ALREADY_EXISTS,
        CUSTOMER_PHONE_NUMBER_ALREADY_EXISTS,
        CUSTOMER_CREATION_FAILED,
        CUSTOMER_UPDATE_FAILED,
        CUSTOMER_DELETION_FAILED,

        // ===== USER EXCEPTION TYPES =====
        USER_NOT_FOUND,
        USER_ALREADY_EXISTS,
        INVALID_CREDENTIALS,
        PASSWORD_HASHING_FAILED,
        USER_CREATION_FAILED,
        USER_UPDATE_FAILED,
        USER_DELETION_FAILED,
        UNAUTHORIZED_ACCESS,

        // ===== ITEM EXCEPTION TYPES =====
        ITEM_NOT_FOUND,
        ITEM_ALREADY_EXISTS,
        INVALID_ITEM_INPUTS,
        ITEM_CREATION_FAILED,
        ITEM_UPDATE_FAILED,
        ITEM_DELETION_FAILED,
        INSUFFICIENT_STOCK,

        // ===== GENERAL EXCEPTIONS =====
        CONFIGURATION_ERROR,
        DATABASE_ERROR,
        INTERNAL_SERVER_ERROR
    }


}
