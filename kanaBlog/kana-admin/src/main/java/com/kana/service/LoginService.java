package com.kana.service;

import com.kana.domain.ResponseResult;
import com.kana.domain.entity.User;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult getInfo();

    ResponseResult getRouters();

    ResponseResult logout();

}
