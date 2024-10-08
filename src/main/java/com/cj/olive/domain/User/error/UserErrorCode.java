package com.cj.olive.domain.User.error;

import com.cj.olive.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    ID_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다."),
    ACCESS_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "accessToken이 오지 않았습니다."),
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, "유효한 JWT 토큰이 아닙니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "해당 로그인 정보가 없습니다."),
    NONE_USER_TYPE(HttpStatus.UNAUTHORIZED, "요청하신 권한을 찾을 수 없습니다. USER, ADMIN 중 하나의 권한을 요청해주세요"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
