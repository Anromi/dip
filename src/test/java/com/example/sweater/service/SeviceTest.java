package com.example.sweater.service;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repos.UserRepo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeviceTest {
    @Autowired
    private UserSevice userSev;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private MailSender mailSen;

    @MockBean
    private PasswordEncoder PasEncod;

    @Test
    public void addUser() {
        User user = new User();
        user.setEmail("jemmes123@mail.ru");
        Assert.assertTrue(userSev.addUser(user));
        Assert.assertNotNull(user.getActivationCode());
        Assert.assertEquals(Collections.singleton(Role.USER), user.getRoles());
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
        Mockito.verify(mailSen, Mockito.times(1))
                .send(ArgumentMatchers.eq(user.getEmail()), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void activateUser() {
        User user = new User();
        user.setActivationCode("ура");
        Mockito.doReturn(user)
                .when(userRepo)
                .findByActivationCode("активировать");
        boolean isUserActivated = userSev.activateUser("активировать");
        Assert.assertTrue(isUserActivated);
        Assert.assertNull(user.getActivationCode());
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }
}