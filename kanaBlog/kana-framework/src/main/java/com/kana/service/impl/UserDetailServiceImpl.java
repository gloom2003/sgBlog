package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kana.constants.SystemConstants;
import com.kana.domain.entity.LoginUser;
import com.kana.domain.entity.User;
import com.kana.mapper.UserMapper;
import com.kana.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 让SpringSecurity从数据库中查询用户信息进行登录验证
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //使用mp的mapper查询用户对象
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,s);
        User user = userMapper.selectOne(queryWrapper);
        if(user==null){
            throw new RuntimeException("用户名或密码错误!");
        }
        //如果是后台用户，才封装用户权限信息
        if(user.getType().equals(SystemConstants.ADMIN)){
            List<String> perms = menuService.selectPermsByUserId(user.getId());
            return new LoginUser(user,perms);
        }

        return new LoginUser(user,null);
    }
}
