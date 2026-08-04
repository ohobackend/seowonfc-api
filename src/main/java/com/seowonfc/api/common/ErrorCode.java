package com.seowonfc.api.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, 40001, "요청 값이 유효하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 40100, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 40300, "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 40400, "리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, 40900, "이미 존재하는 리소스입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50000, "서버 내부 오류입니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}