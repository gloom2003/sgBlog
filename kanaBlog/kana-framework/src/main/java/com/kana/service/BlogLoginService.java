package com.kana.service;

import com.kana.domain.ResponseResult;
import com.kana.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);
    ResponseResult logout();
}
