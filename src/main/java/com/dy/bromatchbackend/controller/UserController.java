package com.dy.bromatchbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dy.bromatchbackend.common.BaseResponse;
import com.dy.bromatchbackend.common.ErrorCode;
import com.dy.bromatchbackend.common.ResultUtils;
import com.dy.bromatchbackend.exception.BusinessException;
import com.dy.bromatchbackend.model.domain.User;
import com.dy.bromatchbackend.model.domain.request.UserLoginRequest;
import com.dy.bromatchbackend.model.domain.request.UserRegisterRequest;
import com.dy.bromatchbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.dy.bromatchbackend.constant.UserConstant.ADMIN_ROLE;
import static com.dy.bromatchbackend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @PostMapping("/outLogin")
    public BaseResponse<Boolean> outLogin(HttpServletRequest request){
        request.getSession().setAttribute(USER_LOGIN_STATE,null);
        return ResultUtils.success(true);
    }

    @GetMapping("/current")
    public BaseResponse<User> currentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        Long id = user.getId();
        //TODO 校验用户是否合法
        //查数据库，因为用户信息可能发生变化
        User currentUser = userService.getById(id);
        User saftyUser = userService.getSaftyUser(currentUser);
        return ResultUtils.success(saftyUser);
    }

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result,"注册成功！");
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user,"登录成功！");
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request){
        // 仅管理员可查询
        if (!isAdmin(request)) throw new BusinessException(ErrorCode.NO_AUTH,"用户无权限查询");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNoneBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(userService::getSaftyUser).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request){
        if (!isAdmin(request)) throw new BusinessException(ErrorCode.NO_AUTH,"用户无权限删除");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录或用户不是管理员");
        }
        return true;
    }
}
