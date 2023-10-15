package com.kana.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddLinkDto;
import com.kana.domain.entity.Link;
import com.kana.domain.vo.LinkVo;
import com.kana.domain.vo.PageVo;
import com.kana.mapper.LinkMapper;
import com.kana.service.LinkService;
import com.kana.utils.BeanCopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2023-07-19 11:16:57
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    /**
     * 前台查询所有的友链
     * @return
     */
    @Override
    public ResponseResult getAllLink() {
        //查询所有审核通过的友链
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> list = list(queryWrapper);
        //封装为Vo
        List<LinkVo> linkVos = BeanCopyUtil.beanListCopy(list, LinkVo.class);

        return ResponseResult.okResult(linkVos);
    }

    /**
     * 后台根据名字与状态查询友链
     * @param pageNum
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    @Override
    public ResponseResult selectLinkByNameOrStatus(Long pageNum, Long pageSize, String name, String status) {
        //分页查询
        Page<Link> page = new Page<>(pageNum,pageSize);
        //设置查询条件
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name),Link::getName,name);
        queryWrapper.eq(StringUtils.hasText(status),Link::getStatus,status);
        //查询与封装vo
        List<Link> links = page(page, queryWrapper).getRecords();
        List<LinkVo> linkVos = BeanCopyUtil.beanListCopy(links, LinkVo.class);
        PageVo pageVo = new PageVo(linkVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 添加友链信息
     * @param addLinkDto
     * @return
     */
    @Override
    public ResponseResult addLink(AddLinkDto addLinkDto) {
        Link link = BeanCopyUtil.beanCopy(addLinkDto, Link.class);
        save(link);
        return ResponseResult.okResult();
    }

    /**
     * 改变友链时回显当前友链的信息
     * @param linkId
     * @return
     */
    @Override
    public ResponseResult selectLinkById(Long linkId) {
        Link link = getById(linkId);
        LinkVo linkVo = BeanCopyUtil.beanCopy(link, LinkVo.class);
        return ResponseResult.okResult(linkVo);
    }

    /**
     * 改变友链信息
     * @param addLinkDto
     * @return
     */
    @Override
    public ResponseResult changeLink(AddLinkDto addLinkDto) {
        Link link = BeanCopyUtil.beanCopy(addLinkDto, Link.class);
        updateById(link);
        return ResponseResult.okResult();
    }

    /**
     * 删除友链
     * @param linkId
     * @return
     */
    @Override
    public ResponseResult deleteLink(Long linkId) {
        removeById(linkId);
        return ResponseResult.okResult();
    }

    /**
     * 改变友链的状态（审核）
     * @param addLinkDto
     * @return
     */
    @Override
    public ResponseResult changeLinkStatus(AddLinkDto addLinkDto) {
        Link link = BeanCopyUtil.beanCopy(addLinkDto, Link.class);
        updateById(link);
        return ResponseResult.okResult();
    }
}

