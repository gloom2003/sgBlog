package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.ResponseResult;
import com.kana.domain.entity.Comment;


/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2023-07-26 16:20:57
 */
public interface CommentService extends IService<Comment> {
    ResponseResult commentList(String commentType,Long articleId, Long pageNum,Long pageSize);
    ResponseResult addComment(Comment comment);
}

