package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddUserDto;
import com.kana.domain.dto.ChangeUserDto;
import com.kana.domain.entity.User;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2023-07-26 20:55:42
 */
public interface UserService extends IService<User> {
    ResponseResult getUserInfo();
    ResponseResult putUserInfo(User user);
    ResponseResult register(User user);
    ResponseResult selectAllUser(Long pageNum,Long pageSize, String userName,Long status,String phoneNumber);
    ResponseResult addUser(AddUserDto addUserDto);
    ResponseResult deleteUserById(Long userId);
    ResponseResult selectUserAndRoleByUserId(Long userId);
    ResponseResult changeUser(AddUserDto addUserDto);
    ResponseResult changeUserStatus(ChangeUserDto changeUserDto);
}

