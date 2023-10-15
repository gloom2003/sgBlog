package com.kana.controller;

import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddArticleDto;
import com.kana.domain.vo.PageVo;
import com.kana.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    //post请求的参数在请求体中
    @PostMapping("/content/article")
    public ResponseResult<Void> insertArticle(@RequestBody AddArticleDto addArticleDto){
        return articleService.insertArticle(addArticleDto);
    }

    @GetMapping("/content/article/list")
    public ResponseResult<PageVo> selectArticle(
            @RequestParam("pageNum") int pageNum,@RequestParam("pageSize") int pageSize,
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "summary",required = false) String summary){
        return articleService.selectArticle(pageNum,pageSize,title,summary);
    }

    @GetMapping("/content/article/{id}")
    public ResponseResult selectArticleById(@PathVariable("id") int id){
        return articleService.selectArticleById(id);
    }

    @PutMapping("/content/article")
    public ResponseResult updateArticle(@RequestBody AddArticleDto addArticleDto){
        return articleService.updateArticle(addArticleDto);
    }

    @DeleteMapping("/content/article/{id}")
    public ResponseResult deleteArticleById(@PathVariable("id") Long id){
        return articleService.deleteArticleById(id);
    }


}
