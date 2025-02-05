package com.example.identity_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1000, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User already existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1004, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    BOOK_NOT_FOUND(1009, "Book not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1010, "Category not found", HttpStatus.NOT_FOUND),
    IMG_EXCEPTION(1011, "Image exception", HttpStatus.INTERNAL_SERVER_ERROR),
    BOOK_UPDATE_FAILED(1012, "Book update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    BOOK_ALREADY_IN_CART(1013, "Book already in your cart", HttpStatus.BAD_REQUEST),
    ERROR_PROCESSING_IMAGE(1014,"Error processing image", HttpStatus.PROCESSING),
    INTERNAL_SERVER_ERROR(1015,"Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND(1016, "User not found", HttpStatus.NOT_FOUND),
    CART_ITEM_EXISTED(1017, "Cart item already existed", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(1018, "Cart not found", HttpStatus.NOT_FOUND),
    POST_NOT_FOUND(1019, "Post not found", HttpStatus.NOT_FOUND),
    COMMENT_SAVE_FAILED(1020, "Comment save failed", HttpStatus.INTERNAL_SERVER_ERROR),
    BOOK_ALREADY_EXISTS(1021, "Book already exists", HttpStatus.BAD_REQUEST),
    PERMISSION_ALREADY_EXISTS(1022, "Permission already exists", HttpStatus.BAD_REQUEST),
    PERMISSION_CREATION_FAILED(1023, "Permission creation failed", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND(1024, "Permission not found", HttpStatus.NOT_FOUND),
    PERMISSION_DELETION_FAILED(1024, "Permission deletion failed", HttpStatus.BAD_REQUEST),
    POST_CREATION_FAILED(1024, "Post creation failed", HttpStatus.BAD_REQUEST),
    CART_EMPTY(1024, "Cart is empty", HttpStatus.BAD_REQUEST),
    BOOK_NOT_ENOUGH_STOCK(1025, "Book not enough stock", HttpStatus.BAD_REQUEST),
    ROLE_CREATION_FAILED(1026, "Role creation failed", HttpStatus.BAD_REQUEST),
    GET_ROLE_FAILED(1027, "Get role failed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1028, "Role not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACTION(1029, "Unauthorized action", HttpStatus.UNAUTHORIZED),
    POST_UPDATE_FAILED(1030, "Post update failed", HttpStatus.BAD_REQUEST),
    POST_DELETION_FAILED(1031, "Post deletion failed", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND(1032, "Comment not found", HttpStatus.NOT_FOUND),
    COMMENT_UPDATE_FAILED(1033, "Comment update failed", HttpStatus.BAD_REQUEST),
    COMMENT_DELETION_FAILED(1034, "Comment deletion failed", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
