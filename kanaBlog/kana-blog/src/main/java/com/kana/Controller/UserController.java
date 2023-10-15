package com.kana.Controller;

import com.kana.annotation.SystemLog;
import com.kana.domain.ResponseResult;
import com.kana.domain.entity.User;
import com.kana.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/userInfo")
    public ResponseResult getUserInfo(){
        return userService.getUserInfo();
    }
    //@PutMapping 和PostMapping作用等同，都是用来向服务器提交信息。如果是添加信息，
    //倾向于用@PostMapping，如果是更新信息，倾向于用@PutMapping
    @SystemLog(BusinessName = "更新用户信息")
    @PutMapping("/userInfo")
    //@RequestBody 告诉mvc请求头中有json格式的数据并赋值给user对象
    public ResponseResult putUserInfo(@RequestBody User user){
        return userService.putUserInfo(user);
    }

    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user){
        return userService.register(user);
    }
}
