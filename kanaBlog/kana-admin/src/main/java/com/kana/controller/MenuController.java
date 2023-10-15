package com.kana.controller;

import com.kana.domain.ResponseResult;
import com.kana.domain.entity.Menu;
import com.kana.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult selectMenuByMenuNameAndStatus(String menuName,Long status){
        return menuService.selectMenuByMenuNameAndStatus(menuName,status);
    }

    @PostMapping
    public ResponseResult insertMenu(@RequestBody Menu menu){
        return menuService.insertMenu(menu);
    }

    @GetMapping("/{id}")
    public ResponseResult selectMenuById(@PathVariable("id") Long menuId){
        return menuService.selectMenuByMenuId(menuId);
    }

    @PutMapping
    public ResponseResult updateMenu(@RequestBody Menu menu){
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{MenuId}")
    public ResponseResult deleteMenuByMenuId(@PathVariable("MenuId") Long MenuId){
        return menuService.deleteMenuByMenuId(MenuId);
    }

    /**
     * 增加角色时回显菜单树
     * @return
     */
    @GetMapping("/treeselect")
    public ResponseResult selectMenuTree(){
        return menuService.selectMenuTree();
    }

    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult roleMenuTreeSelectByRoleId(@PathVariable("id") Long roleId){
        return menuService.roleMenuTreeSelectByRoleId(roleId);
    }
}
