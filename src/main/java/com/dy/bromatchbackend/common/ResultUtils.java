package com.dy.bromatchbackend.common;

public class ResultUtils {
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<T>(0,data,"ok", "");
    }
    public static <T> BaseResponse<T> success(T data,String description){
        return new BaseResponse<T>(0,data,"ok", description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<T>(errorCode);
    }
    public static <T> BaseResponse<T> error(ErrorCode errorCode,String message,String description){
        return new BaseResponse<T>(errorCode.getCode(),null,message, description);
    }
    public static <T> BaseResponse<T> error(int code,String message,String description){
        return new BaseResponse<T>(code,null,message, description);
    }
}