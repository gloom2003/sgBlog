package com.kana.Controller;

import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddCommentDto;
import com.kana.domain.entity.Comment;
import com.kana.service.CommentService;
import com.kana.utils.BeanCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
//swagger的标签注释
@Api(tags = "评论标签",description = "评论相关接口")
public class CommentController {
    @Autowired
    private CommentService commentService;


    @GetMapping("/commentList")
    public ResponseResult commentList(Long articleId,Long pageNum,Long pageSize){
        return commentService.commentList(SystemConstants.ARTICLE_COMMENT,articleId,pageNum,pageSize);
    }

    @PostMapping
    public ResponseResult addComment(@RequestBody AddCommentDto addCommentDto){
        //不使用与数据库进行映射的实体类，而是使用dto类，更加灵活
        Comment comment = BeanCopyUtil.beanCopy(addCommentDto, Comment.class);
        //@RequestBody:告诉springMVC从请求头中获取数据封装到comment对象中
        return commentService.addComment(comment);
    }

    //swagger方法注释
    @ApiOperation(value = "友链评论接口",notes = "获取一页友链评论")
    @GetMapping("/linkCommentList")
    //swagger方法参数注释
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "获取第几页的评论"),
            @ApiImplicitParam(name = "pageSize", value = "一页获取多少条评论")
    }
    )
    public ResponseResult linkCommentList(Long pageNum,Long pageSize){
        return commentService.commentList(SystemConstants.LINK_COMMENT,null,pageNum,pageSize);
    }

}
