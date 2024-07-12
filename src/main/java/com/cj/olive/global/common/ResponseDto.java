package com.cj.olive.global.common;

import com.cj.olive.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ResponseDto {
    private final Integer code;
    private final String message;

    public static ResponseDto of(Integer code) {
        return new ResponseDto(code, HttpStatus.valueOf(code).getReasonPhrase());
    }

    public static ResponseDto of(Integer code, String message) {
        return new ResponseDto(code, message);
    }

    public static ResponseDto of(ErrorCode e) {
        return new ResponseDto(e.getHttpStatus().value(), e.getMessage());
    }
}