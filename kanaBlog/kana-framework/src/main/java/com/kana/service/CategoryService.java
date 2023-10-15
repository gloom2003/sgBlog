package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddCategoryDto;
import com.kana.domain.entity.Category;
import com.kana.domain.vo.CategoryVo;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2023-07-17 19:59:47
 */
public interface CategoryService extends IService<Category> {
    ResponseResult getCategoryList();
    ResponseResult<CategoryVo> listAllCategory();
    ResponseResult selectCategoryByNameOrStatus(Long pageNum,Long pageSize, String name,String status);
    ResponseResult addCategory(AddCategoryDto addCategoryDto);
    ResponseResult getCategoryById(Long categoryId);
    ResponseResult changeCategory(AddCategoryDto addCategoryDto);
    ResponseResult deleteCategory(Long categoryId);
}

