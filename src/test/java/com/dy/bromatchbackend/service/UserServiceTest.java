package com.dy.bromatchbackend.service;
import java.util.Date;

import com.dy.bromatchbackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Test
    public void testAddUser()
    {
        User user = new User();
        user.setUsername("");
        user.setUserAccount("test");
        user.setGender(0);
        user.setUserPassword("1");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        user.setUserRole(0);
        boolean res = userService.save(user);
        System.out.println(user.getId());
        assertTrue(res);
    }
}