package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.entity.LoginUser;
import com.kana.domain.entity.Menu;
import com.kana.domain.entity.Role;
import com.kana.domain.entity.User;
import com.kana.domain.vo.AdminUserInfoVo;
import com.kana.domain.vo.BlogUserLoginVo;
import com.kana.domain.vo.MenuVo;
import com.kana.domain.vo.UserInfoVo;
import com.kana.service.LoginService;
import com.kana.service.MenuService;
import com.kana.service.RoleService;
import com.kana.service.UserService;
import com.kana.utils.BeanCopyUtil;
import com.kana.utils.JwtUtil;
import com.kana.utils.RedisCache;
import com.kana.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SystemLoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;

    /***
     * 登录验证，登录成功则生成token并把用户信息存入Redis，最后返回token
     * @param user
     * @return
     */
    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        //authenticationManager.authenticate方法执行流程图中的方法，
        // 自定义后查询数据库中用户信息并且与传入的信息进行加密对比，验证成功则写入Authentication对象返回，否则返回null
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("authenticationManager.authenticate验证出错：用户名或密码错误！");
        }
        //根据用户id生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String s_id = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(s_id);
        //根据用户id存储到Redis中  后台模块使用login
        redisCache.setCacheObject("login:" + s_id, loginUser);
        //封装为map对象代替Vo对象
        Map<String,String> map = new HashMap<>();
        map.put("token",jwt);
        //封装为ResponseResult类返回
        return ResponseResult.okResult(map);
    }

    /**
     * 获取用户对应的角色的权限信息与用户信息
     * @return
     */
    @Override
    public ResponseResult<AdminUserInfoVo> getInfo() {
        //获取用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        User user = loginUser.getUser();
        UserInfoVo userInfoVo = BeanCopyUtil.beanCopy(user, UserInfoVo.class);
        //获取权限信息 自定义sql于xml中
        List<String> perms = menuService.selectPermsByUserId(user.getId());
        //获取用户的role信息 自定义sql
        List<String> roleKeys = roleService.selectRoleKeyByUserId(user.getId());
        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(perms,roleKeys,userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);
    }

    /**
     * 获取不同用户要显示的菜单界面的信息(动态路由)
     * @return
     */
    @Override
    public ResponseResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        Long roleId = roleService.getRoleIdByUserId(userId);
        List<MenuVo> menuVos = null;
        //1.如果roleId为1(超级管理员)则直接返回所有有效的菜单
        //(因为数据库中sys_role_menu表中 roleId1没有写对应的menuId)
        if(roleId.equals(1L)){
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Menu::getMenuType, SystemConstants.MENU_TYPE, SystemConstants.CATALOG_TYPE);
            queryWrapper.eq(Menu::getStatus, SystemConstants.MENU_STATU_NORMAL);
            queryWrapper.orderByAsc(Menu::getParentId,Menu::getOrderNum);
            List<Menu> list = menuService.list(queryWrapper);
            menuVos = BeanCopyUtil.beanListCopy(list, MenuVo.class);
        }else{//2.否则正常进行查询
            //获取父菜单的信息
            List<Menu> menus  = menuService.selectMenuByUserId(userId);
            menuVos = BeanCopyUtil.beanListCopy(menus, MenuVo.class);
        }
        //给父id为0的菜单的children属性进行赋值,children属性中的children属性又需要赋值
        List<MenuVo> result = buildMenuTree(menuVos,0L);
        Map<String,List<MenuVo>> map = new HashMap<>();
        map.put("menus",result);
        return ResponseResult.okResult(map);

    }

    @Override
    public ResponseResult logout() {
        //获取登录的用户id
        Long userId = SecurityUtils.getUserId();
        //删除用户在Redis中的token信息
        redisCache.deleteObject(SystemConstants.BACKGROUND_REDIS_TOKEN_KEY_PREFIX+userId);
        return ResponseResult.okResult();
    }

    /**
     * 给父id为l的菜单的children属性进行赋值
     * @param menuVos
     * @param l
     * @return
     */
    private List<MenuVo> buildMenuTree(List<MenuVo> menuVos, Long l) {
        return menuVos.stream()
                .filter(menuVo -> menuVo.getParentId().equals(l))
                //首先给第一层的子菜单进行赋值
                .map(menuVo -> menuVo.setChildren(getChildren(menuVo,menuVos)))
                .collect(Collectors.toList());
    }

    /**
     * 递归版本：给父id为l的菜单的children属性进行赋值,返回赋值好的集合
     * @param menuVos
     * @param l
     * @return
     */
    private List<MenuVo> buildMenuTree1(List<MenuVo> menuVos, Long l) {
        return menuVos.stream()
                .filter(menuVo -> menuVo.getParentId().equals(l))
                //递归的进行赋值
                .map(menuVo -> menuVo.setChildren(buildMenuTree1(menuVos,menuVo.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 获取传入菜单的子菜单(children属性已赋值)
     * @param menuVo
     * @param menuVos
     * @return
     */
    private List<MenuVo> getChildren(MenuVo menuVo, List<MenuVo> menuVos) {
        return menuVos.stream()
                .filter(menuVo1 -> menuVo1.getParentId().equals(menuVo.getId()))
                //给第二层的菜单赋值(假如菜单有第3层)
                .map(menuVo1 -> menuVo1.setChildren(getChildren(menuVo1,menuVos)))
                .collect(Collectors.toList());
    }

}