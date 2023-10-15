package com.kana.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddLinkDto;
import com.kana.domain.entity.Link;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2023-07-19 11:16:56
 */
public interface LinkService extends IService<Link> {
    ResponseResult getAllLink();
    ResponseResult selectLinkByNameOrStatus(Long pageNum,Long pageSize, String name,String status);
    ResponseResult addLink(AddLinkDto addLinkDto);
    ResponseResult selectLinkById(Long linkId);
    ResponseResult changeLink(AddLinkDto addLinkDto);
    ResponseResult deleteLink(Long linkId);
    ResponseResult changeLinkStatus(AddLinkDto addLinkDto);
}

