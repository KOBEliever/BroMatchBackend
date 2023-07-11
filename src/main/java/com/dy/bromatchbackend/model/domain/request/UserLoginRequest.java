package com.dy.bromatchbackend.model.domain.request;

import lombok.Data;

/**
 * 用户注册请求体
 */
@Data
public class UserLoginRequest {
    private String userAccount;
    private String userPassword;
}