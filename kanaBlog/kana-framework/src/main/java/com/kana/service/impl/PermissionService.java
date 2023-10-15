package com.kana.service.impl;

import com.kana.constants.SystemConstants;
import com.kana.service.RoleService;
import com.kana.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限判断类，判断当前用户是否有足够的权限进行访问
 */
@Service("ps")
public class PermissionService {
    @Autowired
    private RoleService roleService;
    /**
     * 判断当前用户是否拥有传入的权限
     * @return
     */
    public boolean hasPermissions(String permission) {
        //三更这里写错了 如果用户身份是管理员(RoleId==1)，直接返回true（数据库中没有记录管理员的权限数据）
        Long RoleId = roleService.getRoleIdByUserId(SecurityUtils.getUserId());
        if(RoleId.equals(SystemConstants.ADMIN_USER)){
            return true;
        }
        //否则获取当前用户身份所具有的权限进行对比
        List<String> permissions = SecurityUtils.getLoginUser().getPermissions();
        return permissions.contains(permission);
    }
}
