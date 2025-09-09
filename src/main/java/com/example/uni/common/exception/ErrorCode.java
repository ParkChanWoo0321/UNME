package com.example.uni.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR"),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT"),
    MATCH_CREDITS_EXHAUSTED(HttpStatus.CONFLICT, "매칭 횟수를 모두 소진하였습니다."),
    COUPON_INVALID_OR_EXPIRED(HttpStatus.BAD_REQUEST, "존재하지 않거나 만료된 코드입니다."),
    QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "QUOTA_EXCEEDED"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");

    public final HttpStatus status;
    public final String message;

    ErrorCode(HttpStatus s, String m) {
        this.status = s;
        this.message = m;
    }
}
