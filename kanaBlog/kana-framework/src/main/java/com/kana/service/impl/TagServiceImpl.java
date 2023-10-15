package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.TagListDto;
import com.kana.domain.entity.Tag;
import com.kana.domain.vo.PageVo;
import com.kana.domain.vo.TagVo;
import com.kana.mapper.TagMapper;
import com.kana.service.TagService;
import com.kana.utils.BeanCopyUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2023-08-06 16:04:28
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    /**
     * 根据标签名或备注查询标签信息
     * @param pageNum
     * @param pageSize
     * @param tagDto
     * @return
     */
    @Override
    public ResponseResult selectTag(int pageNum, int pageSize, TagListDto tagDto) {
        String name = tagDto.getName();
        String remark = tagDto.getRemark();
        //根据标签名或备注查询标签信息
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        //name,remark的值有效时才根据值进行like匹配查询,否则直接查询所有的标签
        queryWrapper.like(StringUtils.hasText(name),Tag::getName,name);
        queryWrapper.like(StringUtils.hasText(remark),Tag::getRemark,remark);
        //分页查询
        Page<Tag> page = new Page<>(pageNum,pageSize);
        List<Tag> tags = page(page, queryWrapper).getRecords();
        //拷贝为vo类
        List<TagVo> tagVos = BeanCopyUtil.beanListCopy(tags, TagVo.class);
        //封装结果返回
        return ResponseResult.okResult(new PageVo(tagVos,page.getTotal()));
    }

    /**
     * 添加标签数据到数据库中
     * @param tagListDto
     * @return
     */
    @Override
    public ResponseResult<Void> insertTag(TagListDto tagListDto) {
        Tag tag = new Tag();
        tag.setName(tagListDto.getName());
        tag.setRemark(tagListDto.getRemark());
        save(tag);
        return ResponseResult.okResult();
    }

    /**
     * 删除标签
     * @param id
     * @return
     */
    //设置只有具有特定的权限的用户才能执行删除标签操作
    @PreAuthorize("@ps.hasPermissions('content:tag:remove')")
    @Override
    public ResponseResult<Void> deleteTag(Long id) {
        removeById(id);
        return ResponseResult.okResult();
    }

    /**
     * 根据id获取标签
     * @param id
     * @return
     */
    @Override
    public ResponseResult<TagVo> getTagById(Long id) {
        Tag tag = getById(id);
        TagVo tagVo = BeanCopyUtil.beanCopy(tag, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }

    /**
     * 更新标签数据
     * @param tagListDto
     * @return
     */
    @Override
    public ResponseResult updateTag(TagListDto tagListDto) {
        Tag tag = new Tag();
        tag.setName(tagListDto.getName());
        tag.setRemark(tagListDto.getRemark());
        tag.setId(tagListDto.getId());
        updateById(tag);
        return ResponseResult.okResult();
    }

    /**
     * 查询所有的标签
     * @return
     */
    @Override
    public ResponseResult listAllTag() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        //指定只查询id和name字段
        wrapper.select(Tag::getId,Tag::getName);
        List<Tag> tags = list(wrapper);
        List<TagVo> tagVos = BeanCopyUtil.beanListCopy(tags, TagVo.class);
        return ResponseResult.okResult(tagVos);
    }
}

