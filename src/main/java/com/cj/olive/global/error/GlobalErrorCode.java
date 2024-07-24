package com.cj.olive.global.error;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalErrorCode implements ErrorCode {
    INVALID_ACCESS(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. 요청 형식을 확인해주세요"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인가에 실패하였습니다."),
    AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),
    ACCESS_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "accessToken과 함께 요청해주세요"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "올바른 토큰을 전달해주세요"),
    ONLY_ADMIN_ACCESS(HttpStatus.UNAUTHORIZED, "관리자 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    LOGIN_REQUIRED(HttpStatus.FORBIDDEN, "로그인이 필요합니다."),
    EXPIRED_JWT(HttpStatus.FORBIDDEN, "토큰 유효 시간이 지났습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "요청하신 형식이 정확한지 확인해주세요"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러 입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    GlobalErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
