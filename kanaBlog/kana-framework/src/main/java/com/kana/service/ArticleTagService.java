package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.entity.ArticleTag;


/**
 * 文章标签关联表(ArticleTag)表服务接口
 *
 * @author makejava
 * @since 2023-08-12 15:21:44
 */
public interface ArticleTagService extends IService<ArticleTag> {
    void insert(Long articleId,Long tagId);
}

