package com.kana.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kana.domain.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author makejava
 * @since 2023-08-06 20:46:20
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<String> selectPermsByUserId(Long userId);
    List<Menu> selectMenuByUserId(Long userId);
}

