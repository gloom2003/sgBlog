package com.kana.controller;

import com.kana.domain.ResponseResult;
import com.kana.domain.entity.User;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.exception.SystemException;
import com.kana.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            //会被GlobalExceptionHander的systemExceptionHandler(SystemException e)方法
            //捕获并处理(类似try-catch但是是Controller)
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @GetMapping("/getInfo")
    public ResponseResult getInfo(){
        return loginService.getInfo();
    }

    @GetMapping("/getRouters")
    public ResponseResult getRouters(){
        return loginService.getRouters();
    }
    @PostMapping("/user/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }
}
