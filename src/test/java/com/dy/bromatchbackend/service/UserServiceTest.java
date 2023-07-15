package com.dy.bromatchbackend.service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.dy.bromatchbackend.controller.UserController;
import com.dy.bromatchbackend.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Resource
    private UserController userController;
    @Test
    public void testAddUser()
    {
        User user = new User();
        user.setUsername("admin");
        user.setUserAccount("admin");
        user.setGender(0);
        user.setUserPassword("12345678");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        user.setUserRole(1);
        boolean res = userService.save(user);
        System.out.println(user.getId());
        assertTrue(res);
    }
    @Test
    public void testSearchByTags(){
        List<String> tagNames = Arrays.asList("java","python");
        List<User> userList = userService.searchUsersByTags(tagNames);
        Assert.assertNotNull(userList);
    }
}