package com.cj.olive.domain.Heartbeat.error;

import com.cj.olive.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum HeartbeatErrorCode implements ErrorCode {
    NOT_EXIST_SEARCH_TYPE(HttpStatus.BAD_REQUEST, "해당 SEARCH TYPE은 없습니다. DAY, WEEK, MONTH 값에서 보내주세요."),
    INVALID_DATE(HttpStatus.BAD_REQUEST , "유효한 날짜를 입력하세요."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    HeartbeatErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
