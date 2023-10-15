package com.kana.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddArticleDto;
import com.kana.domain.dto.AddCategoryDto;
import com.kana.domain.entity.Article;
import com.kana.domain.entity.Category;
import com.kana.domain.entity.Tag;
import com.kana.domain.vo.CategoryVo;
import com.kana.domain.vo.ExcelCategoryVo;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.service.ArticleService;
import com.kana.service.CategoryService;
import com.kana.utils.BeanCopyUtil;
import com.kana.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/content/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult<CategoryVo> listAllCategory(){
        return categoryService.listAllCategory();
    }

    /**
     * 导出为Excel文件功能
     * @param response
     */
    //设置调用此方法需要的权限(调用ps类的hasPermissions()方法判断是否具有相应的权限)
    @PreAuthorize("@ps.hasPermissions('system:user:export')")
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        try {
            //设置下载文件时的响应头中的字段
            WebUtils.setDownLoadHeader("文章分类数据.xlsx",response);
            //获取分类数据
            List<Category> categories = categoryService.list();
            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtil.beanListCopy(categories, ExcelCategoryVo.class);
            //写入数据到Excel中，响应中返回文件数据而不是json数据
            //设置写入的实体对象
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class)
                    //设置自动关闭流失效
                    .autoCloseStream(Boolean.FALSE)
                    //设置工作簿名称
                    .sheet("分类导出")
                    //设置写入的具体数据
                    .doWrite(excelCategoryVos);
        } catch (Exception e) {
            //出现异常则返回json数据
            e.printStackTrace();
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            //设置写入响应的数据
            WebUtils.renderString(response, JSON.toJSONString(result));
        }

    }

    /**
     * 根据分类名与状态查询分类信息
     * @param pageNum
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    @GetMapping("/list")
    public ResponseResult selectCategoryByNameOrStatus(Long pageNum,Long pageSize,
                                                       String name,String status){
        return categoryService.selectCategoryByNameOrStatus(pageNum,pageSize,name,status);
    }

    /**
     * 添加分类功能
     * @param addCategoryDto
     * @return
     */
    @PostMapping
    public ResponseResult addCategory(@RequestBody AddCategoryDto addCategoryDto){
        return categoryService.addCategory(addCategoryDto);
    }

    /**
     * 回显当前id的分类数据
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult getCategoryById(@PathVariable("id") Long categoryId){
        return categoryService.getCategoryById(categoryId);
    }

    /**
     * 改变分类信息
     * @return
     */
    @PutMapping
    public ResponseResult changeCategory(@RequestBody AddCategoryDto addCategoryDto){
        return categoryService.changeCategory(addCategoryDto);
    }

    /**
     * 逻辑删除分类
     * @param categoryId
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteCategory(@PathVariable("id") Long categoryId){
        return categoryService.deleteCategory(categoryId);
    }
}
