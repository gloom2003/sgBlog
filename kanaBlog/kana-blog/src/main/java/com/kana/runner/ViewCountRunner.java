package com.kana.runner;

import com.kana.constants.SystemConstants;
import com.kana.domain.entity.Article;
import com.kana.mapper.ArticleMapper;
import com.kana.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * implements CommandLineRunner接口来指示springBoot程序启动时要做的一些事情
 */
@Component
public class ViewCountRunner implements CommandLineRunner {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 程序启动时将数据库中每篇文章的访问量的数据存储到map中最后存储到Redis中
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        //获取所有的文章列表
        List<Article> articles = articleMapper.selectList(null);
        //存储在map集合中
        Map<String, Integer> map = articles.stream()
                .collect(Collectors.toMap(article -> article.getId().toString()
                        //value值转换为Integer类型而不是Long，后面才能进行递增
                        , article -> article.getViewCount().intValue()));
        //存储到Redis中
        //使用Hash结构把map存储在Redis中
        redisCache.setCacheMap(SystemConstants.VIEWCOUNT_MAP_KEY, map);

        //redisTemplate.opsForValue().set(SystemConstants.VIEWCOUNT_MAP_KEY,map);

    }
}
