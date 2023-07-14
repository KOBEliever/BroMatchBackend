package com.dy.bromatchbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.bromatchbackend.common.ErrorCode;
import com.dy.bromatchbackend.exception.BusinessException;
import com.dy.bromatchbackend.service.UserService;
import com.dy.bromatchbackend.model.domain.User;
import com.dy.bromatchbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dy.bromatchbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author LEGION
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-07-10 22:32:11
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Resource
    private UserMapper userMapper;
    /**
     * 盐值：混淆密码
     */
    private static final String SALT = "dy";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1. 校验
        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        //长度
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度过短");
        }
        if(userPassword.length()<8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度过短");
        }
        //账户不能包含特殊字符
        String pattern = ".*[*?!&￥$%^#,./@\";:><\\]\\[}{\\-=+_\\\\|》《。，、？’‘“”~`）].*$";
        if(Pattern.matches(pattern, userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含特殊字符");
        }
        //密码和校验密码相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码相同");
        }
        //账户不能重复 (查询了数据库，这个校验应该放到最后校验)
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("userAccount",userAccount);
        long count = this.count(qw);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已注册");
        }
        //2.加密
        String md5Password = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(md5Password);
        boolean result = this.save(user);//保存user成功后，会把id塞给user
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_EXCEPTION,"注册失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1. 校验
        // 非空
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        //长度
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度过短");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度过短");
        }
        //账户不能包含特殊字符
        String pattern = ".*[*?!&￥$%^#,./@\";:><\\]\\[}{\\-=+_\\\\|》《。，、？’‘“”~`）].*$";
        if(Pattern.matches(pattern, userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含特殊字符");
        }
        //2.加密
        String md5Password = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户是否存在
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("userAccount",userAccount).eq("userPassword",md5Password);
        User user = userMapper.selectOne(qw);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码不正确");
        }
        //3.用户脱敏
        User safetyUser = getSaftyUser(user);
        //4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSaftyUser(User user) {
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        return safetyUser;
    }
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接and查询数据
        //like'%Java%' and like'%C++%'
        for(String tagName : tagNameList){
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSaftyUser).collect(Collectors.toList());
    }
}




