package com.dy.bromatchbackend.exception;

import com.dy.bromatchbackend.common.BaseResponse;
import com.dy.bromatchbackend.common.ErrorCode;
import com.dy.bromatchbackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//Spring AOP
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
        log.error("businessException: "+e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException: ", e);
        return ResultUtils.error(ErrorCode.SYSTEM_EXCEPTION);
    }
}
