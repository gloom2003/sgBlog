package com.kana.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kana.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;


/**
 * 角色和菜单关联表(RoleMenu)表数据库访问层
 *
 * @author makejava
 * @since 2023-08-18 12:07:36
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    void insert(@Param("roleId") Long roleId,@Param("menuId") Long menuId);
}

