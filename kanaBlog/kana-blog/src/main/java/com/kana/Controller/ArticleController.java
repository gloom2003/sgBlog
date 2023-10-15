package com.kana.Controller;

import com.kana.domain.ResponseResult;
import com.kana.domain.entity.Article;
import com.kana.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController()
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

//    @GetMapping("/test")
//    public List<Article> test(){
//        return articleService.list();
//    }
    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList() {
        return articleService.hotArticleList();
    }

    @GetMapping("/articleList")//query参数类型
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId){
        return articleService.articleList(pageNum,pageSize,categoryId);
    }
    //key-value形式传参
    @GetMapping("/{id}")//{id}为占位符，@PathVariable("id")注解获取路径上面的id作为参数赋值给Long id
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        return articleService.getArticleDetail(id);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        return articleService.updateViewCount(id);
    }
}
