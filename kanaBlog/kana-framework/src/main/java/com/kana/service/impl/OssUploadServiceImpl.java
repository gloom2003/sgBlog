package com.kana.service.impl;

import com.google.gson.Gson;
import com.kana.domain.ResponseResult;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.exception.SystemException;
import com.kana.service.UploadService;
import com.kana.utils.PathUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.io.FileInputStream;
import java.io.InputStream;

@Service
@Data
//设置读取前缀,类中的属性会从application.yml中进行读取并使用set方法给当前类的属性进行赋值
@ConfigurationProperties(prefix = "oss")
public class OssUploadServiceImpl implements UploadService {
    private String accessKey;
    private String secretKey;
    private String bucket;

    /**
     * 接收前端上传的图片(MultipartFile对象)存储到七牛云的图床并把图片的链接(url)响应给前端
     */
    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        //判断上传的文件类型
        String originalFilename = img.getOriginalFilename();
        assert originalFilename != null;
        //只能上传png格式
        if(!originalFilename.endsWith(".png")){
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }
        //生成文件的存储路径（原始路径+新的文件名）
        String filePath = PathUtils.generateFilePath(originalFilename);
        //存储到七牛云的图床中并返回url
        String url = uploadOSS(img,filePath);
        return ResponseResult.okResult(url);
    }

    /**
     * 接收前端上传的图片(MultipartFile对象)和并指定绝对路径(filePath)存储到七牛云的图床路径中并把图片的外链(url)进行返回
     * @param img
     * @return
     */
    private String uploadOSS(MultipartFile img,String filePath){

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filePath;

        try {

            InputStream inputStream = img.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return "http://s2k69te8d.hn-bkt.clouddn.com/"+key;
            } catch (QiniuException ex) {
                ex.printStackTrace();
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    ex2.printStackTrace();
                    //ignore
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //ignore
        }
        return "...";

    }
}
