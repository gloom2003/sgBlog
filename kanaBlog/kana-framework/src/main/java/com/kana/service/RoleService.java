package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddRoleDto;
import com.kana.domain.dto.ChangeRoleDto;
import com.kana.domain.entity.Role;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2023-08-06 20:18:16
 */
public interface RoleService extends IService<Role> {
    List<String> selectRoleKeyByUserId(Long userId);

    Long getRoleIdByUserId(Long userId);

    ResponseResult selectRoleByRoleNameOrStatus(Integer pageNum, Integer pageSize, String roleName, Integer status);

    ResponseResult changeStatus(ChangeRoleDto changeRoleDto);

    ResponseResult addRole(AddRoleDto addRoleDto);

    ResponseResult selectRoleByRoleId(Long roleId);

    ResponseResult changeRole(AddRoleDto addRoleDto);

    ResponseResult deleteRole(Long roleId);

    ResponseResult selectAllRole();
}

