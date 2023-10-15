package com.kana.Controller;

import com.kana.domain.ResponseResult;
import com.kana.domain.entity.User;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.exception.SystemException;
import com.kana.service.BlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogLoginController {
    @Autowired
    BlogLoginService loginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            //会被GlobalExceptionHander的systemExceptionHandler(SystemException e)方法
            //捕获并处理(类似try-catch配合Controller)
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @PostMapping("/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }

}
