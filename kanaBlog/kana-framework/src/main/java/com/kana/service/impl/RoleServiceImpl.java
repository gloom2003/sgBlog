package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddRoleDto;
import com.kana.domain.dto.ChangeRoleDto;
import com.kana.domain.entity.Menu;
import com.kana.domain.entity.Role;
import com.kana.domain.entity.RoleMenu;
import com.kana.domain.vo.*;
import com.kana.mapper.RoleMapper;
import com.kana.service.MenuService;
import com.kana.service.RoleMenuService;
import com.kana.service.RoleService;
import com.kana.utils.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2023-08-06 20:18:17
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMenuService roleMenuService;


    @Override
    public List<String> selectRoleKeyByUserId(Long userId) {

        return getBaseMapper().selectRoleKeyByUserId(userId);
    }

    @Override
    public Long getRoleIdByUserId(Long userId) {
        return getBaseMapper().getRoleIdByUserId(userId);
    }

    /**
     * 用户根据角色名称或状态搜索角色信息
     *
     * @param pageNum
     * @param pageSize
     * @param roleName
     * @param status
     * @return
     */
    @Override
    public ResponseResult selectRoleByRoleNameOrStatus(Integer pageNum, Integer pageSize, String roleName, Integer status) {
        //分页查询
        Page<Role> page = new Page<>(pageNum, pageSize);
        //设置查询条件
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(roleName), Role::getRoleName, roleName);
        queryWrapper.eq(Objects.nonNull(status), Role::getStatus, status);
        //排序
        queryWrapper.orderByAsc(Role::getRoleSort);
        page(page, queryWrapper);
        //封装vo
        List<Role> roles = page.getRecords();
        List<RoleVo> roleVos = BeanCopyUtil.beanListCopy(roles, RoleVo.class);
        PageVo pageVo = new PageVo(roleVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 修改角色的状态
     *
     * @param changeRoleDto
     * @return
     */
    @Override
    public ResponseResult changeStatus(ChangeRoleDto changeRoleDto) {
        Role role = new Role();
        role.setId(changeRoleDto.getRoleId());
        role.setStatus(changeRoleDto.getStatus());
        updateById(role);
        return ResponseResult.okResult();
    }

    /**
     * 添加新的角色
     *
     * @param addRoleDto
     * @return
     */
    @Override
    public ResponseResult addRole(AddRoleDto addRoleDto) {
        //添加信息到角色表
        Role role = BeanCopyUtil.beanCopy(addRoleDto, Role.class);
        //role表id进行了自增
        save(role);
        //添加权限信息到菜单角色表
        List<Long> menuIds = addRoleDto.getMenuIds();
        for (Long menuId : menuIds) {
            roleMenuService.insert(role.getId(), menuId);
        }
        return ResponseResult.okResult();
    }

    /**
     * 点击修改时回显角色的相关信息
     *
     * @param roleId
     * @return
     */
    @Override
    public ResponseResult selectRoleByRoleId(Long roleId) {
        Role role = getById(roleId);
        RoleVo roleVo = BeanCopyUtil.beanCopy(role, RoleVo.class);
        return ResponseResult.okResult(roleVo);
    }


    /**
     * 改变角色的信息并且改变拥有的菜单权限
     *
     * @param addRoleDto
     * @return
     */
    @Override
    public ResponseResult changeRole(AddRoleDto addRoleDto) {
        //改变角色的信息
        Role role = BeanCopyUtil.beanCopy(addRoleDto, Role.class);
        updateById(role);
        //改变(删除然后新增)拥有的菜单权限
        //删除 delete语句
        roleMenuService.removeById(addRoleDto.getId());
        //新增
        List<Long> menuIds = addRoleDto.getMenuIds();
        menuIds
                .forEach(menuId->roleMenuService.insert(addRoleDto.getId(),menuId));
        return ResponseResult.okResult();
    }

    /**
     * 逻辑删除角色信息
     *
     * @param roleId
     * @return
     */
    @Override
    public ResponseResult deleteRole(Long roleId) {
        removeById(roleId);
        return ResponseResult.okResult();
    }

    /**
     * 新增用户时显示所有的角色信息给用户进行选择
     * @return
     */
    @Override
    public ResponseResult selectAllRole() {
        //查询状态正常的角色
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus,SystemConstants.ROLE_STATUS_NORMAL);
        List<Role> roles = list(queryWrapper);
        List<UserRoleVo> userRoleVos = BeanCopyUtil.beanListCopy(roles, UserRoleVo.class);
        return ResponseResult.okResult(userRoleVos);
    }


}

