package com.kana.controller;

import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddUserDto;
import com.kana.domain.dto.ChangeUserDto;
import com.kana.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("system/user/list")
    public ResponseResult selectAllUser(Long pageNum,Long pageSize, String userName,Long status,
                                        @RequestParam(value = "phonenumber",required = false) String phoneNumber){
        return userService.selectAllUser(pageNum,pageSize,userName,status,phoneNumber);
    }

    @PostMapping("system/user")
    public ResponseResult addUser(@RequestBody AddUserDto addUserDto){
        return userService.addUser(addUserDto);
    }

    @DeleteMapping("/system/user/{id}")
    public ResponseResult deleteUserById(@PathVariable("id") Long userId){
        return userService.deleteUserById(userId);
    }

    /**
     * 回显对应id的用户信息与角色信息
     * @return
     */
    @GetMapping("/system/user/{id}")
    public ResponseResult selectUserAndRoleByUserId(@PathVariable("id") Long userId){
        return userService.selectUserAndRoleByUserId(userId);
    }

    /**
     * 改变用户状态（是否停用）
     * @param changeUserDto
     * @return
     */
    @PutMapping("/system/user/changeStatus")
    public ResponseResult changeUserStatus(@RequestBody ChangeUserDto changeUserDto){
        return userService.changeUserStatus(changeUserDto);
    }

    /**
     * 修改用户信息
     * @param addUserDto
     * @return
     */
    @PutMapping("/system/user")
    public ResponseResult changeUser(@RequestBody AddUserDto addUserDto){
        return userService.changeUser(addUserDto);
    }

}
