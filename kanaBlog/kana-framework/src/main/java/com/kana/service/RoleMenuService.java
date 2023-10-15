package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;


/**
 * 角色和菜单关联表(RoleMenu)表服务接口
 *
 * @author makejava
 * @since 2023-08-18 12:07:37
 */
public interface RoleMenuService extends IService<RoleMenu> {

    void insert(@Param("roleId") Long roleId,@Param("menuId") Long menuId);

}

