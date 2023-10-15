package com.kana.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kana.domain.entity.ArticleTag;
import org.apache.ibatis.annotations.Param;


/**
 * 文章标签关联表(ArticleTag)表数据库访问层
 *
 * @author makejava
 * @since 2023-08-12 15:21:43
 */
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
    void insert(@Param("articleId") Long articleId, @Param("tagId") Long tagId);
}

