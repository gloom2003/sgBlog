package com.kana;


import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
//注意：想要正常启动测试类的话，测试类的包名需要与启动类的包名一致，否则需要进行配置启动类的字节码对象
// 如：@SpringBootTest(KanaBlogApplication.class)
@SpringBootTest
//设置读取前缀,类中的属性会从application.yml中进行读取并使用set方法进行赋值
//@ConfigurationProperties(prefix = "oss")
public class MainTest {
    private String accessKey;
    private String secretKey;
    private String bucket;

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Test
    public void testOSS(){

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
//        String accessKey = "-UNA25Q9-m-hrdlXi412YljfYbCO3PgceqmpPA54";
//        String secretKey = "_nsQZKm4RMUaiDOzd5Z_BAOe4IdNZZ6xxsOCsRAW";
//        String bucket = "ysj-1";

//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "kana.png";

        try {
//            byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
//            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);

            InputStream inputStream = new FileInputStream(
                    "C:\\Users\\GLOOM\\Desktop\\for zip\\image-20230711115252118.png");
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (QiniuException ex) {
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

    }
}
