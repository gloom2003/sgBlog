package com.kana.controller;

import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddRoleDto;
import com.kana.domain.dto.ChangeRoleDto;
import com.kana.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/system/role/list")
    public ResponseResult selectRoleByRoleNameOrStatus(Integer pageNum,
                                                       Integer pageSize,String roleName,Integer status){
        return roleService.selectRoleByRoleNameOrStatus(pageNum,pageSize,roleName,status);
    }

    @PutMapping("/system/role/changeStatus")
    public ResponseResult changeStatus(@RequestBody ChangeRoleDto changeRoleDto){
        return roleService.changeStatus(changeRoleDto);
    }

    @PostMapping("/system/role")
    public ResponseResult addRole(@RequestBody AddRoleDto addRoleDto){
        return roleService.addRole(addRoleDto);
    }

    @GetMapping("/system/role/{id}")
    public ResponseResult selectRoleByRoleId(@PathVariable("id") Long roleId){
        return roleService.selectRoleByRoleId(roleId);
    }


    @PutMapping("/system/role")
    public ResponseResult changeRole(@RequestBody AddRoleDto addRoleDto){
        return roleService.changeRole(addRoleDto);
    }

    @DeleteMapping("/system/role/{id}")
    public ResponseResult deleteRole(@PathVariable("id") Long roleId){
        return roleService.deleteRole(roleId);
    }

    @GetMapping("/system/role/listAllRole")
    public ResponseResult selectAllRole(){
        return roleService.selectAllRole();
    }
}
