package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddCategoryDto;
import com.kana.domain.entity.Article;
import com.kana.domain.entity.Category;
import com.kana.domain.vo.CategoryVo;
import com.kana.domain.vo.PageVo;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.exception.SystemException;
import com.kana.mapper.CategoryMapper;
import com.kana.service.ArticleService;
import com.kana.service.CategoryService;
import com.kana.utils.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2023-07-17 19:59:49
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    ArticleService articleService;
    @Override
    public ResponseResult getCategoryList() {
        //查询文章表，status为正常状态的文章
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> list = articleService.list(queryWrapper);
        //查询文章的分类id,去重后获得所有文章的分类id
        Set<Long> set = list.stream()
                .map(article -> article.getCategoryId())
                .collect(Collectors.toSet());
        //根据分类id,status正常查询分类表,获得对应的实体类
        List<Category> categories = listByIds(set);
        categories = categories.stream()
                .filter(category -> SystemConstants.CATEGORY_STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());
        //拷贝为vo对象封装到ResponseResult中进行返回
        List<CategoryVo> categoryVos = BeanCopyUtil.beanListCopy(categories, CategoryVo.class);
        return ResponseResult.okResult(categoryVos);
    }

    /**
     * 查询状态正常的所有分类
     * @return
     */
    @Override
    public ResponseResult<CategoryVo> listAllCategory() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus,SystemConstants.CATEGORY_STATUS_NORMAL);
        List<Category> categories = list(queryWrapper);
        List<CategoryVo> categoryVos = BeanCopyUtil.beanListCopy(categories, CategoryVo.class);
        return ResponseResult.okResult(categoryVos);
    }

    /**
     * 根据分类名与状态查询分类信息
     * @param pageNum
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    @Override
    public ResponseResult selectCategoryByNameOrStatus(Long pageNum, Long pageSize,
                                                       String name, String status) {
        //分页查询
        Page<Category> page = new Page<>(pageNum,pageSize);
        //设置查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name),Category::getName,name);
        queryWrapper.eq(StringUtils.hasText(status),Category::getStatus,status);
        //查询并封装vo
        List<Category> categories = page(page, queryWrapper).getRecords();
        List<CategoryVo> categoryVos = BeanCopyUtil.beanListCopy(categories, CategoryVo.class);
        PageVo pageVo = new PageVo(categoryVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 添加分类功能,分类名不能重复
     * @param addCategoryDto
     * @return
     */
    @Override
    public ResponseResult addCategory(AddCategoryDto addCategoryDto) {
        //分类名不能重复
        if(isCategoryNameExist(addCategoryDto.getName())){
            throw new SystemException(AppHttpCodeEnum.CATEGORY_EXIST);
        }
        //添加分类
        Category category = BeanCopyUtil.beanCopy(addCategoryDto, Category.class);
        save(category);
        return ResponseResult.okResult();
    }

    /**
     * 回显当前id的分类数据
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult getCategoryById(Long categoryId) {
        Category category = getById(categoryId);
        CategoryVo categoryVo = BeanCopyUtil.beanCopy(category, CategoryVo.class);
        return ResponseResult.okResult(categoryVo);
    }

    /**
     * 改变分类信息
     * @param addCategoryDto
     * @return
     */
    @Override
    public ResponseResult changeCategory(AddCategoryDto addCategoryDto) {
        Category category = BeanCopyUtil.beanCopy(addCategoryDto, Category.class);
        updateById(category);
        return ResponseResult.okResult();
    }

    /**
     * 逻辑删除分类
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult deleteCategory(Long categoryId) {
        removeById(categoryId);
        return ResponseResult.okResult();
    }

    private boolean isCategoryNameExist(String categoryName) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName,categoryName);
        return count(queryWrapper) > 0;
    }
}

