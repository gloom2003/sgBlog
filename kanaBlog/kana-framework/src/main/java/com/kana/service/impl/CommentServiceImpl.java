package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.entity.Comment;
import com.kana.domain.entity.User;
import com.kana.domain.vo.CommentVo;
import com.kana.domain.vo.PageVo;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.exception.SystemException;
import com.kana.mapper.CommentMapper;
import com.kana.mapper.UserMapper;
import com.kana.service.CommentService;
import com.kana.service.UserService;
import com.kana.utils.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-07-26 16:20:57
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private UserService userService;

    @Override
    public ResponseResult commentList(String commentType,Long articleId, Long pageNum,Long pageSize) {

        //分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        //设置查询评论的文章id、查询根评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, SystemConstants.ROOT_COMMENT_ID);
        //相当于动态sql：SystemConstants.ARTICLE_COMMENT.equals(commentType)为true时
        // 评论类型为文章评论时，这个条件才会生效
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType)
                ,Comment::getArticleId,articleId);
        queryWrapper.eq(Comment::getType,commentType);
        page(page, queryWrapper);
        //拷贝为vo类,vo中多出来的字段进行sql查询赋值
        List<CommentVo> commentVos = toCommentVoList(page.getRecords());
        //查询数据库给List<CommentVo> children进行赋值  查询子评论进行sql查询赋值
        commentVos = commentVos.stream()
                .map(this::getChildren)
                .collect(Collectors.toList());
        //封装ResponseResult对象
        return ResponseResult.okResult(new PageVo(commentVos,page.getTotal()));
    }

    /***
     * 添加评论功能：把评论添加相应的字段后存储到数据库中
     * @param comment
     * @return
     */
    @Override
    public ResponseResult addComment(Comment comment) {
        if(!StringUtils.hasText(comment.getContent())){
            //评论为空则封装 SystemException交给全局异常处理器进行处理
            throw new SystemException(AppHttpCodeEnum.COMMENT_IS_EMPTY);
        }
        save(comment);
        return ResponseResult.okResult();
    }

    /***
     * 给commentVo的List<CommentVo> children进行赋值
     * @param commentVo
     * @return
     */
    private CommentVo getChildren(CommentVo commentVo){
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId,commentVo.getId());
        queryWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> list = list(queryWrapper);
        List<CommentVo> commentVos = toCommentVoList(list);
        commentVo.setChildren(commentVos);
        return commentVo;
    }

    /***
     * 进行赋值，把List<Comment>改变为List<CommentVo>
     * @param list
     * @return
     */
    private List<CommentVo> toCommentVoList(List<Comment> list){
        List<CommentVo> commentVos = BeanCopyUtil.beanListCopy(list, CommentVo.class);
        return commentVos.stream()
                .map(this::apply)
                .collect(Collectors.toList());
    }

    /***
     * 给CommentVo中自定义的username，toCommentUsername两个字段进行查询赋值
     * @param commentVo
     * @return
     */
    private CommentVo apply(CommentVo commentVo) {
        String username = userService.getById(commentVo.getCreateBy()).getNickName();
        commentVo.setUsername(username);
        Long toCommentUserId = commentVo.getToCommentUserId();
        //有回复有被回复方id才进行查询   判断是否有回复，以查询接受回复方的id，否则只能查出默认值
        if(!toCommentUserId.equals(SystemConstants.NOT_REPLY)){
            String toCommentUsername = userService.getById(toCommentUserId).getNickName();
            commentVo.setToCommentUserName(toCommentUsername);
        }
        return commentVo;
    }
}

