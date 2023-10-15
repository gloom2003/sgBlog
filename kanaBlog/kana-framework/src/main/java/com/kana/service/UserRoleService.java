package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.entity.UserRole;


/**
 * 用户和角色关联表(UserRole)表服务接口
 *
 * @author makejava
 * @since 2023-08-19 17:25:57
 */
public interface UserRoleService extends IService<UserRole> {

    void insert(Long userId, Long roleId);

}

