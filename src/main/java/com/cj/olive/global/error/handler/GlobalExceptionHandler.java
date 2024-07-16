package com.cj.olive.global.error.handler;


import com.cj.olive.global.common.ResponseDto;
import com.cj.olive.global.error.ErrorCode;
import com.cj.olive.global.error.GlobalErrorCode;
import com.cj.olive.global.error.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // AppException
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(AppException e) {
        int code = e.getErrorCode().getHttpStatus().value();
        String message = e.getMessage();

        // 서버 에러일 경우 스택 트레이스 출력
        if (code == 500)
            e.printStackTrace();

        return ResponseEntity.status(code).body(ResponseDto.of(code, message));
    }

    // MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(MethodArgumentNotValidException e) {
        int code = HttpStatus.BAD_REQUEST.value();
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(code).body(ResponseDto.of(code, message));
    }

    // MethodNotAllowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(HttpRequestMethodNotSupportedException e) {
        ErrorCode errorCode = GlobalErrorCode.METHOD_NOT_ALLOWED;
        int code = errorCode.getHttpStatus().value();
        String message = errorCode.getMessage();

        return ResponseEntity.status(code).body(ResponseDto.of(code, message));
    }

    // InternalServerError
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> exceptionHandler(Exception e) {
        ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        int code = errorCode.getHttpStatus().value();
        String message = errorCode.getMessage();

        e.printStackTrace();

        return ResponseEntity.status(code).body(ResponseDto.of(code, message));
    }
}

