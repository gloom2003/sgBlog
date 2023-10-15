package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.TagListDto;
import com.kana.domain.entity.Tag;
import com.kana.domain.vo.TagVo;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2023-08-06 16:04:28
 */
public interface TagService extends IService<Tag> {
    ResponseResult selectTag(int pageNum, int pageSize, TagListDto tagDto);
    ResponseResult<Void> insertTag(TagListDto tagListDto);
    ResponseResult<Void> deleteTag(Long id);
    ResponseResult<TagVo> getTagById(Long id);
    ResponseResult updateTag(TagListDto tagListDto);
    ResponseResult listAllTag();
}

