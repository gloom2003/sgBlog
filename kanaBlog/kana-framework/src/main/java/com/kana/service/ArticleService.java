package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddArticleDto;
import com.kana.domain.entity.Article;
import com.kana.domain.vo.PageVo;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();
    ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId);
    ResponseResult getArticleDetail(Long id);
    ResponseResult updateViewCount(Long id);
    ResponseResult<Void> insertArticle(AddArticleDto addArticleDto);
    ResponseResult<PageVo> selectArticle(int pageNum, int pageSize, String title, String summary);
    ResponseResult selectArticleById(int id);
    ResponseResult updateArticle(AddArticleDto addArticleDto);
    ResponseResult deleteArticleById(Long id);
    void updateViewCountToMysql(Long articleId,Long viewCount);
}
