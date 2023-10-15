package com.kana.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.domain.entity.UserRole;
import com.kana.mapper.UserRoleMapper;
import com.kana.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户和角色关联表(UserRole)表服务实现类
 *
 * @author makejava
 * @since 2023-08-19 17:25:57
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public void insert(Long userId, Long roleId) {
        getBaseMapper().insert(userId,roleId);
    }
}

