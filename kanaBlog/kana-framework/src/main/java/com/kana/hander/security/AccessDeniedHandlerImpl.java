package com.kana.hander.security;

import com.alibaba.fastjson.JSON;
import com.kana.domain.ResponseResult;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.utils.WebUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***
 * 自定义授权失败的异常处理器  AccessDeniedHandler访问被拒绝处理器
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler{
    /***
     * 自定义没有权限操作时返回给前端的响应数据
     * @param httpServletRequest
     * @param httpServletResponse
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException, ServletException {
        e.printStackTrace();
        ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        WebUtils.renderString(httpServletResponse, JSON.toJSONString(result));
    }
}
