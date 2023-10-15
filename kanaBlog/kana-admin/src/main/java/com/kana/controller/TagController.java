package com.kana.controller;

import com.kana.domain.ResponseResult;
import com.kana.domain.dto.TagListDto;
import com.kana.domain.vo.PageVo;
import com.kana.domain.vo.TagVo;
import com.kana.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult<PageVo> selectTag(@RequestParam("pageNum") int pageNum,
                                            @RequestParam("pageSize") int pageSize,
                                            TagListDto tagDto){

        return tagService.selectTag(pageNum,pageSize,tagDto);
    }

    @PostMapping
    public ResponseResult<Void> insertTag(@RequestBody TagListDto tagListDto){
        //@RequestBody:告诉springMVC从请求头中获取数据封装到tagListDto对象中
        return tagService.insertTag(tagListDto);
    }

    @DeleteMapping("/{id}")
    //{id}为占位符，@PathVariable("id")注解获取路径上面的id作为参数赋值给Long id
    public ResponseResult<Void> deleteTag(@PathVariable("id") Long id){
        return tagService.deleteTag(id);
    }
    //RestFul风格的api,根据请求发送来确定api的作用
    @GetMapping("/{id}")
    public ResponseResult<TagVo> getTagById(@PathVariable("id") Long id){
        return tagService.getTagById(id);
    }

    @PutMapping
    public ResponseResult updateTag(@RequestBody TagListDto tagListDto){
        return tagService.updateTag(tagListDto);
    }

    @GetMapping("/listAllTag")
    public ResponseResult listAllTag(){
        return tagService.listAllTag();
    }
}
