package com.kana.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.domain.entity.RoleMenu;
import com.kana.mapper.RoleMenuMapper;
import com.kana.service.RoleMenuService;
import org.springframework.stereotype.Service;

/**
 * 角色和菜单关联表(RoleMenu)表服务实现类
 *
 * @author makejava
 * @since 2023-08-18 12:07:38
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    public void insert(Long roleId, Long menuId) {
        getBaseMapper().insert(roleId,menuId);
    }
}

