package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddUserDto;
import com.kana.domain.dto.ChangeUserDto;
import com.kana.domain.entity.Role;
import com.kana.domain.entity.User;
import com.kana.domain.entity.UserRole;
import com.kana.domain.vo.*;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.exception.SystemException;
import com.kana.mapper.UserMapper;
import com.kana.service.RoleService;
import com.kana.service.UserRoleService;
import com.kana.service.UserService;
import com.kana.utils.BeanCopyUtil;
import com.kana.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-07-26 20:55:42
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;
    /**
     * 获取个人中心的用户信息
     * 注意：想要保证数据的实时性必须从数据库中进行查询，直接使用Redis存储在SecurityContext中的数据没有实时性，
     * 一旦数据库的数据发生更改，除非登录信息过期然后重新查询数据库，否则Redis存储在SecurityContext中的数据不会发生变化
     *
     * @return
     */
    @Override
    public ResponseResult getUserInfo() {
        //获取登录的用户id
        Long id = SecurityUtils.getUserId();
        //查询数据库获取用户对象
        User user = getById(id);
        //拷贝为userInfoVo对象并返回
        UserInfoVo userInfoVo = BeanCopyUtil.beanCopy(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    /**
     * 保存用户信息到数据库中,执行update语句,设置有默认值所以不需要执行insert语句
     *
     * @param user
     * @return
     */
    @Override
    public ResponseResult putUserInfo(User user) {
        //对数据是否重复进行判断,个人中心中邮箱不允许，所以不需要判断邮箱
//        if (isEmailExist(user.getEmail())) {
////            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
////        }
        //更新用户数据
        updateById(user);
        return ResponseResult.okResult();
    }

    /**
     * 注册功能
     *
     * @param user
     * @return
     */
    @Override
    public ResponseResult register(User user) {
        //后端对数据进行非空判断，防止技术人员找到请求接口后绕过前端进行访问
        if (!StringUtils.hasText(user.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_ILLEGAL);
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_ILLEGAL);
        }
        if (!StringUtils.hasText(user.getNickName())) {
            throw new SystemException(AppHttpCodeEnum.NICKNAME_ILLEGAL);
        }
        if (!StringUtils.hasText(user.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_ILLEGAL);
        }
        //对数据是否重复进行判断
        if (isUsernameExist(user.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (isEmailExist(user.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        //把密码进行加密处理后再存储到数据库中，明文存储不安全
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
        return ResponseResult.okResult();
    }

    /**
     * 后台查询所有的用户信息
     * @param pageNum
     * @param pageSize
     * @param userName
     * @param status
     * @param phoneNumber
     * @return
     */
    @Override
    public ResponseResult selectAllUser(Long pageNum, Long pageSize,
                                        String userName,Long status, String phoneNumber) {
        //分页查询
        Page<User> page = new Page<>(pageNum,pageSize);
        //设置条件进行查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(userName),User::getUserName,userName);
        queryWrapper.like(StringUtils.hasText(phoneNumber),User::getPhonenumber,phoneNumber);
        queryWrapper.eq(Objects.nonNull(status),User::getStatus,status);
        page(page,queryWrapper);
        //封装vo
        List<User> records = page.getRecords();
        List<UserVo> userVos = BeanCopyUtil.beanListCopy(records, UserVo.class);
        PageVo pageVo = new PageVo(userVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 后台添加新用户功能
     * @param addUserDto
     * @return
     */
    @Override
    public ResponseResult addUser(AddUserDto addUserDto) {
        //判断用户名、邮箱、手机号是否重复
        if (isUsernameExist(addUserDto.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(isEmailExist(addUserDto.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        if(isPhoneNumberExist(addUserDto.getPhonenumber())){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        User user = BeanCopyUtil.beanCopy(addUserDto, User.class);
        //密码进行加密存储
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //添加用户 记得设置自动fill时间 生成的id已自动赋值给user对象
        save(user);
        //给用户添加角色的权限信息
        List<Long> roleIds = addUserDto.getRoleIds();
        roleIds.forEach(roleId->userRoleService.insert(user.getId(),roleId));
        return ResponseResult.okResult();
    }

    /**
     * 逻辑删除此id的用户信息
     * @param userId
     * @return
     */
    @Override
    public ResponseResult deleteUserById(Long userId) {
        removeById(userId);
        return ResponseResult.okResult();
    }

    /**
     * 回显对应id的用户信息与角色信息
     * @param userId
     * @return
     */
    @Override
    public ResponseResult selectUserAndRoleByUserId(Long userId) {
        //查询用户信息
        User user = getById(userId);
        //查询当前用户的角色id信息
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,userId);
        List<UserRole> userRoles = userRoleService.list(queryWrapper);
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        //查询所有的角色信息
        List<Role> roles = roleService.list();
        List<UserRoleVo> userRoleVos = BeanCopyUtil.beanListCopy(roles, UserRoleVo.class);
        //封装vo
        UpdateUserVo updateUserVo = new UpdateUserVo(roleIds,userRoleVos,user);
        return ResponseResult.okResult(updateUserVo);
    }

    @Override
    public ResponseResult changeUser(AddUserDto addUserDto) {
        User user = BeanCopyUtil.beanCopy(addUserDto, User.class);
        updateById(user);
        return ResponseResult.okResult();
    }

    // 修改了bug：接口写错了，这个接口没有写
    @Override
    public ResponseResult changeUserStatus(ChangeUserDto changeUserDto) {
        User user = new User();
        user.setId(changeUserDto.getUserId());
        user.setStatus(changeUserDto.getStatus());
        updateById(user);
        return ResponseResult.okResult();
    }


    private boolean isUsernameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        return count(queryWrapper) > 0;
    }

    private boolean isEmailExist(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,email);
        return count(queryWrapper) > 0;
    }

    private boolean isPhoneNumberExist(String phoneNumber) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhonenumber,phoneNumber);
        return count(queryWrapper) > 0;
    }


}

