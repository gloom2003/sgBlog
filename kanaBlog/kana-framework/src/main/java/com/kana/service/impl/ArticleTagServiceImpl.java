package com.kana.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.domain.entity.ArticleTag;
import com.kana.mapper.ArticleTagMapper;
import com.kana.service.ArticleTagService;
import org.springframework.stereotype.Service;

/**
 * 文章标签关联表(ArticleTag)表服务实现类
 *
 * @author makejava
 * @since 2023-08-12 15:21:45
 */
@Service("articleTagService")
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {

    @Override
    public void insert(Long articleId,Long tagId) {
        getBaseMapper().insert(articleId,tagId);
    }
}

