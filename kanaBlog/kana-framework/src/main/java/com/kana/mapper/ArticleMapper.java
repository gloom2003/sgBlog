package com.kana.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kana.domain.entity.Article;
import com.kana.domain.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {
    void updateViewCountToMysql(@Param("articleId") Long articleId,@Param("viewCount") Long viewCount);
}
