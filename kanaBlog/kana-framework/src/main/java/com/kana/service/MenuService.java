package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.ResponseResult;
import com.kana.domain.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2023-08-06 20:46:22
 */
public interface MenuService extends IService<Menu> {
    List<String> selectPermsByUserId(Long userId);
    List<Menu> selectMenuByUserId(Long userId);
    ResponseResult selectMenuByMenuNameAndStatus(String menuName, Long status);
    ResponseResult insertMenu(Menu menu);
    ResponseResult selectMenuByMenuId(Long menuId);
    ResponseResult updateMenu(Menu menu);
    ResponseResult deleteMenuByMenuId(Long MenuId);
    ResponseResult selectMenuTree();
    ResponseResult roleMenuTreeSelectByRoleId(Long roleId);
}

