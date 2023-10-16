package com.kana.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * swagger文档信息配置,设置上半部分的信息
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //配置swagger的包扫描路径
                .apis(RequestHandlerSelectors.basePackage("com.kana.Controller"))
                .build();
    }

    /**
     * 配置swagger文档的介绍信息
     * @return
     */
    private ApiInfo apiInfo() {
        Contact contact = new Contact("kana", "http://www.kana.com", "13431141161@163.com");
        return new ApiInfoBuilder()
                .title("前后端分离的博客系统")
                .description("后端基于SpringBoot2.5.0 + mybatis-plus + Redis + SpringSecurity,前端基于vue进行开发")
                .contact(contact)   // 联系方式
                .version("1.1.0")  // 版本
                .build();
    }
}