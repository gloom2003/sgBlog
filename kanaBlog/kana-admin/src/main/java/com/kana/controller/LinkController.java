package com.kana.controller;

import com.kana.domain.ResponseResult;
import com.kana.domain.dto.AddLinkDto;
import com.kana.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {
    @Autowired
    private LinkService linkService;

    /**
     * 后台根据名字与状态查询友链
     * @param pageNum
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    @GetMapping("/list")
    public ResponseResult selectLinkByNameOrStatus(Long pageNum,Long pageSize,
                                                   String name,String status){
        return linkService.selectLinkByNameOrStatus(pageNum,pageSize,name,status);
    }

    /**
     * 添加友链信息
     * @param addLinkDto
     * @return
     */
    @PostMapping
    public ResponseResult addLink(@RequestBody AddLinkDto addLinkDto){
        return linkService.addLink(addLinkDto);
    }

    /**
     * 改变友链时回显当前友链的信息
     * @param linkId
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult selectLinkById(@PathVariable("id") Long linkId){
        return linkService.selectLinkById(linkId);
    }

    /**
     * 改变友链信息
     * @param addLinkDto
     * @return
     */
    @PutMapping
    public ResponseResult changeLink(@RequestBody AddLinkDto addLinkDto){
        return linkService.changeLink(addLinkDto);
    }

    /**
     * 删除友链
     * @param linkId
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteLink(@PathVariable("id") Long linkId){
        return linkService.deleteLink(linkId);
    }

    /**
     * 改变友链的状态（审核）
     * @param addLinkDto
     * @return
     */
    @PutMapping("/changeLinkStatus")
    public ResponseResult changeLinkStatus(@RequestBody AddLinkDto addLinkDto){
        return linkService.changeLinkStatus(addLinkDto);
    }
}
