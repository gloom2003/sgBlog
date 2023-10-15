package com.kana.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kana.domain.entity.UserRole;
import org.apache.ibatis.annotations.Param;


/**
 * 用户和角色关联表(UserRole)表数据库访问层
 *
 * @author makejava
 * @since 2023-08-19 17:25:55
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {
    void insert(@Param("userId") Long userId,@Param("roleId") Long roleId);
}

