package com.kana.controller;

import com.kana.domain.ResponseResult;
import com.kana.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UploadController {
    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseResult<String> upload(@RequestParam("img") MultipartFile img){
        //从http请求中提取名为“img”的 MultipartFile类型的参数，并将之作为输入参数传入img
        //img,值为要上传的文件
        //前端的请求头：Content-Type ：multipart/form-data;
        try {
            return uploadService.uploadImg(img);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败！");
        }
    }
}
