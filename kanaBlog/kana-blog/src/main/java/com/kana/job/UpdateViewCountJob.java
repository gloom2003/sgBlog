package com.kana.job;

import com.kana.constants.SystemConstants;
import com.kana.domain.entity.Article;
import com.kana.service.ArticleService;
import com.kana.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateViewCountJob {
    @Autowired
    //继承了IService,提供了许多批量操作的方法
    private ArticleService articleService;

    @Autowired
    private RedisCache redisCache;

    /**
     *
     * cron表达式的使用
     * 1.允许的特殊字符：, - * /    四个字符
     * 2.日期和星期两个部分如果其中一个部分设置了值，则另一个必须设置为 “ ? ”。 即：
     * * 例如：
     *      *
     *      *        日期  星期
     *      * 0\* * * 2 * ?
     *      *  和
     *      * 0\* * * ? * 2
     *      *
     * 3.cron表达式由七部分组成（一般只写6部分），中间由空格分隔，其中最后一个部分(年)一般该项不设置，直接忽略掉，即可为空值

     每隔1分钟的第1秒把Redis中的访问量数据写入数据库中
     */
    @Scheduled(cron = "1 0/1 * * * ?")
    public void updateViewCount() {
        Map<String, Integer> viewCountMap = redisCache.getCacheMap(SystemConstants.VIEWCOUNT_MAP_KEY);
        List<Article> articles = viewCountMap.entrySet()
                .stream()
                .map(entry -> {
                            return new Article(Long.valueOf(entry.getKey()), entry.getValue().longValue());
                        }
                )
                .collect(Collectors.toList());
        //对于updateBatchById方法，如果批量更新的实体对象属性为空，
        // 则不会对数据库中对应的字段进行更新，即数据库中原有的值会保持不变。
        // 使用updateBatchById(articles);或updateById()都会报错：java.lang.NullPointerException
        //articleService.updateBatchById(articles);

        //只能使用自定义的update语句
        articles.forEach(article -> articleService.updateViewCountToMysql(article.getId(),article.getViewCount()));

    }
}
