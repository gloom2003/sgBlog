package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddArticleDto;
import com.kana.domain.entity.Article;
import com.kana.domain.entity.ArticleTag;
import com.kana.domain.entity.Category;
import com.kana.domain.vo.*;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.exception.SystemException;
import com.kana.mapper.ArticleMapper;
import com.kana.service.ArticleService;
import com.kana.service.ArticleTagService;
import com.kana.service.CategoryService;
import com.kana.utils.BeanCopyUtil;
import com.kana.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

//mybatis-plus的使用
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取热门文章列表的信息
     *
     * @return
     */
    @Override
    public ResponseResult hotArticleList() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //最好使用方法引用Article::getStatus而不是字符串"status",
        //条件+排序
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Article::getViewCount);
        //分页查询
        Page<Article> page = new Page<>(SystemConstants.SEARCH_ONE_PAGE, SystemConstants.SEARCH_COUNT);
        page(page, queryWrapper);
        List<Article> orders = page.getRecords();
        //从Redis中获取viewCount，实现viewCount实时变化
        List<Article> articles = orders.stream()
                .map(article -> {
                    Integer viewCount = redisCache.getCacheMapValue(SystemConstants.VIEWCOUNT_MAP_KEY,
                            article.getId().toString());
                    if(Objects.isNull(viewCount)){
                        //设置出现异常后的值
                        viewCount = 0;
                        throw new SystemException(AppHttpCodeEnum.VIEW_COUNT_IS_NULL);
                    }
                    return article.setViewCount(viewCount.longValue());
                })
                .collect(Collectors.toList());
        //Bean拷贝；拷贝为只包含前端需要字段的类,过滤返回给前端的数据
        //1.0版本：
//        List<HotArticleVo> articleVos = new ArrayList<>();
//        for (Article order : orders) {
//            HotArticleVo hotArticleVo = new HotArticleVo();
//            //根据类的类型与名字进行拷贝
//            BeanUtils.copyProperties(order,hotArticleVo);
//            articleVos.add(hotArticleVo);
//        }
        //2.0封装版本
        List<HotArticleVo> articleVos = BeanCopyUtil.beanListCopy(articles, HotArticleVo.class);
        return ResponseResult.okResult(articleVos);
    }


    /**
     * 获取指定分类的文章列表并根据置顶的顺序进行返回
     *
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //分页查询,需要写一个配置类才能生效（容易忘记）
        Page<Article> page = new Page<>(pageNum, pageSize);
        //判断categoryId是否有值，前端是否传输
        //必须指定泛型！！！
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper();
        //Objects.nonNull(categoryId)判断categoryId不是null,返回true表示不是null
        queryWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId);
        //文章状态需要正常，置顶的文章需要排在前面
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Article::getIsTop);
        page(page, queryWrapper);
        //单独查询分类名字并且进行赋值 for循环版本
//        List<Article> records = page.getRecords();
//        for (Article article : records) {
//            Category category = categoryService.getById(article.getCategoryId());
//            article.setCategoryName(category.getName());
//        }

        //单独查询分类名字并且进行赋值 stream流版本
        List<Article> records = page.getRecords();
        List<Article> collect = records.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());
        //封装为Vo，两次封装
        //page.getRecords()与collect指向同一个区域，甚至可以不接收collect
        List<ArticleListVo> articleListVos = BeanCopyUtil.beanListCopy(page.getRecords(), ArticleListVo.class);
        PageVo pageVo = new PageVo(articleListVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 根据id获取指定文章的详细信息
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult getArticleDetail(Long id) {
        //用户访问文章详情，则根据文章id查询文章
        Article article = getById(id);
        //浏览量增加

        //浏览量增加后，从Redis中获取viewCount，实现viewCount实时变化
        Integer viewCount = redisCache.getCacheMapValue(SystemConstants.VIEWCOUNT_MAP_KEY, id.toString());
        article.setViewCount(viewCount.longValue());
        //封装成为Vo
        ArticleDetailVo articleDetailVo = BeanCopyUtil.beanCopy(article, ArticleDetailVo.class);
        //根据分类id查询分类名并且给Vo添加上
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if (category != null) {//避免category空指针异常
            articleDetailVo.setCategoryName(category.getName());
        }
        return ResponseResult.okResult(articleDetailVo);
    }

    /**
     * 更新Redis中存储的访问量ViewCount数据
     *  设置对应的文章浏览量加1（前端会自动调用此接口的Controller）
     * @param id
     * @return
     */
    @Override
    public ResponseResult updateViewCount(Long id) {
        //使Redis中的map的value自增1
        redisCache.incrementCacheMapValue(SystemConstants.VIEWCOUNT_MAP_KEY, id.toString(), 1);
        return ResponseResult.okResult();
    }

    /**
     * 插入文章信息并写入文章的标签信息,
     *  @param addArticleDto
     * @return
     * 在方法或类上添加@Transactional注解后，Spring会为其生成代理对象，通过该对象来管理事务的开启、提交或回滚。
     */
    @Override
    @Transactional
    public ResponseResult<Void> insertArticle(AddArticleDto addArticleDto) {
        Article article = BeanCopyUtil.beanCopy(addArticleDto, Article.class);
        // TODO 以后优化：给置顶功能设置需要具有相应的权限才能实现
//        if("1".equals(article.getIsTop())){
//            if(!permissionService.hasPermissions("content:article:isTop")){
//                //但是前端不显示封装在AppHttpCodeEnum.NO_ARTICLE_TOP_PERMISSION中的信息
//                throw new SystemException(AppHttpCodeEnum.NO_ARTICLE_TOP_PERMISSION);
//            }
//        }
        //插入文章信息
        //使用mp的save方法时，新生成的文章的id已经赋值给传入的article对象了
        save(article);
        //把新添加的文章的浏览量(0)数据存储到Redis的viewCount的Map的hash结构中
        redisCache.setCacheMapValue(SystemConstants.VIEWCOUNT_MAP_KEY,article.getId().toString(),0);
        //添加文章的标签信息
        List<ArticleTag> articleTags = addArticleDto.getTags().stream()
                .map(tag -> new ArticleTag(article.getId(), tag))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

//    /**
//     * 设置文章是否置顶，需要相应的权限才能够执行
//     *
//     */
//    @PreAuthorize("@ps.hasPermissions('content:article:isTop')")
//    private void articleIsTopPermission(){
//
//    }


    /**
     * 可根据标题与摘要搜索文章信息
     *
     * @param pageNum
     * @param pageSize
     * @param title
     * @param summary
     * @return
     */
    @Override
    public ResponseResult<PageVo> selectArticle(int pageNum, int pageSize, String title, String summary) {
        //设置分页查询
        Page<Article> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        //设置查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.like(StringUtils.hasText(title), Article::getTitle, title);
        queryWrapper.like(StringUtils.hasText(summary), Article::getSummary, summary);

        page(page, queryWrapper);
        //查询并封装vo进行返回
        List<Article> records = page.getRecords();
        List<ArticleVo> articleVos = BeanCopyUtil.beanListCopy(records, ArticleVo.class);
        PageVo pageVo = new PageVo(articleVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 根据id查询文章信息，随便返回文章的标签信息
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult selectArticleById(int id) {
        Article article = getById(id);
        ArticleTagVo articleTagVo = BeanCopyUtil.beanCopy(article, ArticleTagVo.class);
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, id);
        List<ArticleTag> list = articleTagService.list(queryWrapper);
        List<Long> tagIds = list.stream()
                .map(ArticleTag::getTagId)
                .collect(Collectors.toList());
        articleTagVo.setTags(tagIds);
        return ResponseResult.okResult(articleTagVo);
    }

    /**
     * 更新文章表的信息与文章标签表的信息
     *
     * @param addArticleDto
     * @return
     */
    @Override
    public ResponseResult updateArticle(AddArticleDto addArticleDto) {
        //更新文章表的信息
        Article article = BeanCopyUtil.beanCopy(addArticleDto, Article.class);
        updateById(article);
        //更新文章标签表的信息
        List<Long> tags = addArticleDto.getTags();
        List<ArticleTag> articleTags = tags.stream()
                .map(tag -> new ArticleTag(article.getId(), tag))
                .collect(Collectors.toList());
        //首先删除文章标签表中id相等的所有信息 delete语句
        articleTagService.removeById(article.getId());
        //然后插入所有的文章标签信息
        // 抛异常无异常信息，自定义的sql为什么不能成功插入
        // 数据库可以正常运行INSERT INTO sg_article_tag(article_id,tag_id) VALUES(5,3);
        //原因：没有添加@Param注解指定Map的key
        for (ArticleTag articleTag : articleTags) {
            articleTagService.insert(articleTag.getArticleId(),articleTag.getTagId());
        }
        return ResponseResult.okResult();
    }

    /**
     * 删除文章(逻辑删除)
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteArticleById(Long id) {
        // 执行update语句：UPDATE sg_article SET del_flag=1 WHERE id=? AND del_flag=0
        removeById(id);
        return ResponseResult.okResult();
    }

    @Override
    public void updateViewCountToMysql(Long articleId, Long viewCount) {
        getBaseMapper().updateViewCountToMysql(articleId,viewCount);
    }


}
