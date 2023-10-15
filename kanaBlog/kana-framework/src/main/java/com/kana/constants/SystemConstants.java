package com.kana.constants;

public class SystemConstants
{
    /**
     *  文章是草稿
     */
    public static final int ARTICLE_STATUS_DRAFT = 1;
    /**
     *  文章是正常分布状态
     */
    public static final int ARTICLE_STATUS_NORMAL = 0;
    /***
     * 查询第一页
     */
    public static final int SEARCH_ONE_PAGE = 1;
    /***
     * 查询的条数
     */
    public static final int SEARCH_COUNT = 10;

    public static final int CROS_ALLOW_TIME = 3600;
    /***
     * 分类为正常状态
     */
    public static final String CATEGORY_STATUS_NORMAL = "0";
    /***
     * 文章审核已经通过
     */
    public static final int LINK_STATUS_NORMAL = 0;
    /***
     * 表示此评论为根评论
     */
    public static final int ROOT_COMMENT_ID = -1;
    /**
     * 表示没有对评论进行回复，不是子评论
     */
    public static final Long NOT_REPLY = -1L;
    /**
     * 评论类型为文章评论
     */
    public static final String ARTICLE_COMMENT = "0";
    /**
     * 评论类型为友链评论
     */
    public static final String LINK_COMMENT = "1";
    /**
     * 存放viewCount的map的key
     */
    public static final String VIEWCOUNT_MAP_KEY = "article:viewCount";
    /**
     * 菜单状态正常
     */
    public static final int MENU_STATU_NORMAL = 0;
    /**
     * 菜单类型
     */
    public static final String MENU_TYPE = "C";
    /**
     * 按钮类型
     */
    public static final String BUTTON_TYPE = "F";
    /**
     * 身份为admin
     */
    public static final Long ADMIN_USER = 1L;
    /**
     * 目录类型
     */
    public static final String CATALOG_TYPE = "M";
    /**
     * 后台项目中存放在Redis中的token的key
     */
    public static final String BACKGROUND_REDIS_TOKEN_KEY_PREFIX = "login:";
    /**
     * 前台项目中存放在Redis中的token的key
     */
    public static final String RECEPTION_REDIS_TOKEN_KEY_PREFIX = "bloglogin:";
    /**
     * 后台管理员
     */
    public static final String ADMIN = "1";

    /**
     * 数据被删除
     */
    public static final Integer DATA_IS_DELETE = 1;

    /**
     * 角色状态正常
     */
    public static final Long ROLE_STATUS_NORMAL = 0L;

}