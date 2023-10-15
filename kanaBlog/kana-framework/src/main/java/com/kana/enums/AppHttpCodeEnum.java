package com.kana.enums;

public enum AppHttpCodeEnum {

    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"出现错误"),
    USERNAME_EXIST(501,"用户名已存在"),
     PHONENUMBER_EXIST(502,"手机号已存在"),
    EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_USERNAME(504, "必须填写用户名"),
    LOGIN_ERROR(505,"用户名或密码错误"),
    COMMENT_IS_EMPTY(506,"评论内容不能为空"),
    FILE_TYPE_ERROR(507,"文件类型错误，请上传png格式"),
    USERNAME_ILLEGAL(508,"用户名非法"),
    PASSWORD_ILLEGAL(509,"密码非法"),
    EMAIL_ILLEGAL(510,"邮箱非法"),
    NICKNAME_ILLEGAL(511,"昵称非法"),
    NICKNAME_EXIST(512,"昵称已存在"),
    CATEGORY_EXIST(513,"此分类已存在"),
    HAVE_CHILDREN_MENU_NOT_APPLY_DELETE(514,"存在子菜单不允许删除"),
    VIEW_COUNT_IS_NULL(515,"从Redis中获取的浏览量为null"),
    NO_ARTICLE_TOP_PERMISSION(516,"你没有文章置顶的权限");

    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
