package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.entity.Menu;
import com.kana.domain.entity.RoleMenu;
import com.kana.domain.vo.MenuRoleVo;
import com.kana.domain.vo.MenuTreeVo;
import com.kana.domain.vo.MenuVo;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.mapper.MenuMapper;
import com.kana.service.MenuService;
import com.kana.service.RoleMenuService;
import com.kana.service.RoleService;
import com.kana.utils.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-08-06 20:46:22
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 通过用户id查询权限信息
     * @param userId
     * @return
     */
    @Override
    public List<String> selectPermsByUserId(Long userId) {
        Long roleId = roleService.getRoleIdByUserId(userId);
        //三更这里写错了 如果是超级管理员（roleId=1）则返回所有有效的权限(未被删除，状态正常，类型为目录和按钮)
        if (roleId.equals(SystemConstants.ADMIN_USER)) {
            //单表查询，使用mp即可
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Menu::getStatus, SystemConstants.MENU_STATU_NORMAL);
            queryWrapper.in(Menu::getMenuType,SystemConstants.MENU_TYPE,SystemConstants.BUTTON_TYPE);
            List<Menu> list = list(queryWrapper);
            List<String> perms = list.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        //否则返回它所具有的权限  获取MenuMapper对象并使用其中自定义的selectPermsByUserId（）方法
        return getBaseMapper().selectPermsByUserId(userId);
    }

    @Override
    public List<Menu> selectMenuByUserId(Long userId) {

        return getBaseMapper().selectMenuByUserId(userId);
    }

    /**
     * 输入菜单名或选择状态来查询相应的菜单信息
     * @param menuName
     * @param status
     * @return
     */
    @Override
    public ResponseResult selectMenuByMenuNameAndStatus(String menuName, Long status) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件
        queryWrapper.like(StringUtils.hasText(menuName),Menu::getMenuName,menuName);
        queryWrapper.eq(status!=null,Menu::getStatus,status);
        queryWrapper.orderByAsc(Menu::getParentId,Menu::getOrderNum);
        List<Menu> menus = list(queryWrapper);
        //封装vo
        List<MenuVo> menuVos = BeanCopyUtil.beanListCopy(menus, MenuVo.class);
        return ResponseResult.okResult(menuVos);
    }

    /**
     * 添加新的菜单列
     * @param menu
     * @return
     */
    @Override
    public ResponseResult insertMenu(Menu menu) {
        save(menu);
        return ResponseResult.okResult();
    }

    /**
     * 根据菜单id查询菜单信息
     * @param menuId
     * @return
     */
    @Override
    public ResponseResult selectMenuByMenuId(Long menuId) {
        Menu menu = getById(menuId);
        MenuVo menuVo = BeanCopyUtil.beanCopy(menu, MenuVo.class);
        return ResponseResult.okResult(menuVo);
    }

    /**
     * 修改菜单信息，其中限制父菜单不能选择自己
     * @param menu
     * @return
     */
    @Override
    public ResponseResult updateMenu(Menu menu) {
        //限制父菜单不能选择自己
        if(menu.getParentId().equals(menu.getId())){
            return ResponseResult.errorResult(500,"修改菜单'"+menu.getMenuName()+"'失败，上级菜单不能选择自己");
        }
        updateById(menu);
        return ResponseResult.okResult();
    }

    /**
     * 根据菜单id（逻辑）删除菜单信息,注意删除菜单有子菜单时不能进行删除
     * @param MenuId
     * @return
     */
    @Override
    public ResponseResult deleteMenuByMenuId(Long MenuId) {
        //判断要删除的菜单是否有子菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId,MenuId);
        List<Menu> menus = list(queryWrapper);
        //查询不到结果时返回空列表
        if(menus.size()!=0){
            return ResponseResult.errorResult(AppHttpCodeEnum.HAVE_CHILDREN_MENU_NOT_APPLY_DELETE);
        }
        //没有则进行逻辑删除
        removeById(MenuId);
        return ResponseResult.okResult();
    }

    /**
     * 查询菜单树(父菜单与子菜单所构成的树)的结构
     * @return
     */
    @Override
    public ResponseResult selectMenuTree() {
        List<Menu> menus = list();
        //构造菜单树(给父菜单id为0的菜单赋好值，包括children的children属性)
        List<MenuRoleVo> menuRoleVos = buildMenuTree(menus, 0L);
        return ResponseResult.okResult(menuRoleVos);
    }


    /**
     * 显示菜单树并回显（选中）当前角色拥有的的菜单权限
     * @param roleId
     * @return
     */
    @Override
    public ResponseResult roleMenuTreeSelectByRoleId(Long roleId) {
        List<Menu> menus = list();
        //查询菜单树
        List<MenuRoleVo> menuRoleVos =  buildMenuTree(menus,0L);
        //查询角色拥有的菜单权限信息
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId,roleId);
        List<RoleMenu> roleMenus = roleMenuService.list(queryWrapper);
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
        //封装vo
        MenuTreeVo menuTreeVo = new MenuTreeVo(menuRoleVos,menuIds);
        return ResponseResult.okResult(menuTreeVo);
    }

    /**
     * 构造以父菜单id(num)为首的菜单树
     * @param menus
     * @param num
     * @return
     */
    private List<MenuRoleVo> buildMenuTree(List<Menu> menus, Long num) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(num))
                .map(this::copyAndSet)
                .map(menuRoleVo -> menuRoleVo.setChildren(getChildren(menus,menuRoleVo.getId())))
                .collect(Collectors.toList());
    }


    /**
     * 递归获取children属性
     * @param menus
     * @param menuId
     * @return
     */
    private List<MenuRoleVo> getChildren(List<Menu> menus, Long menuId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(menuId))
                .map(this::copyAndSet)
                .map(menuRoleVo -> menuRoleVo.setChildren(getChildren(menus,menuRoleVo.getId())))
                .collect(Collectors.toList());

    }

    /**
     * 拷贝与赋值
     * @param menu
     * @return
     */
    private MenuRoleVo copyAndSet(Menu menu){
        MenuRoleVo menuRoleVo = BeanCopyUtil.beanCopy(menu, MenuRoleVo.class);
        menuRoleVo.setLabel(menu.getMenuName());
        return menuRoleVo;
    }
}

