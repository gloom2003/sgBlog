package com.kana;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//快捷键： Alt + 键盘左键 标签左右切换
@SpringBootApplication
@MapperScan("com.kana.mapper")
//开启定时任务的支持，写在配置类中，Application也算是一个配置类
@EnableScheduling
//开启swagger的支持
@EnableSwagger2
public class KanaBlogApplication {
    public static void main(String[] args){
        SpringApplication.run(KanaBlogApplication.class,args);
    }
}
