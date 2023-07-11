package com.dy.bromatchbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
