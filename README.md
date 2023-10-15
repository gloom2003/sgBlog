# 博客项目介绍

## 1 整体效果

### 1.1 博客前台:

![https://image.itbaima.net/images/173/image-2023101520330798.png](https://image.itbaima.net/images/173/image-2023101520330798.png)

![https://image.itbaima.net/images/173/image-20231015203026307.png](https://image.itbaima.net/images/173/image-20231015203026307.png)

### 1.2 博客后台

![https://image.itbaima.net/images/173/image-20231015202600783.png](https://image.itbaima.net/images/173/image-20231015202600783.png)

![https://image.itbaima.net/images/173/image-20231015205472307.png](https://image.itbaima.net/images/173/image-20231015205472307.png)

![https://image.itbaima.net/images/173/image-2023101520957385.png](https://image.itbaima.net/images/173/image-2023101520957385.png)

![https://image.itbaima.net/images/173/image-20231015219707599.png](https://image.itbaima.net/images/173/image-20231015219707599.png)

## 2 整体结构

整个博客项目分为**博客前台**与**博客后台**，博客前台主要是文章的阅读，功能包含查看不同分类的文章、赞赏页面、展示友链、评论功能。后台主要是对前台的文章、登录的用户、各种权限进行管理，包含写博文、系统管理、内容管理等模块。

sg-blog-vue与sg-vue-admin分别是博客前台与博客后台的**前端代码**。kanaBlog文件夹包含前后台的**后端代码**，整体是一个多模块项目，其中kana-blog为博客前台的后端代码，kana-admin为博客后台的后端代码，这两个都依赖于kana-framework模块。

## 3 运行方法

### 3.1 运行前端代码：

1.下载npm，版本不要使用最新版本，低2+个版本最好，比如最新版本为16.0，下载10-13大概率都可以。

2.在cmd中打开文件夹(第一次记得使用管理员身份运行)，以运行sg-blog-vue项目为例，在cmd中打开sg-blog-vue文件夹,依次运行下面的代码即可

3.运行sg-vue-admin同理

``` bash
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

```

### 3.2 运行后端代码方法

1.改application.yml文件(配置数据库，Redis)，在数据库中运行kana_blog.sql文件创建数据库(名称为sg_blog)，在本地启动Redis,运行后端代码即可(两个启动类都是这样操作)

2.在kana-admin的resources目录下的application.yml下面，配置有一个oss属性，是用来存储图片的七牛云的密钥，想要使用存储图片功能请替换为自己的密钥或者使用其他的oss存储服务。

