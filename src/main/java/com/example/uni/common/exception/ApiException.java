package com.example.uni.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ErrorCode code;
    public ApiException(ErrorCode code){ super(code.message); this.code = code; }
}
