package com.kana.hander.security;

import com.alibaba.fastjson.JSON;
import com.kana.domain.ResponseResult;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.utils.WebUtils;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***
 * 自定义认证失败的异常处理器
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    /***
     * 登录失败时自定义返回给前端的信息
     * @param httpServletRequest
     * @param httpServletResponse
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        //打印错误信息
        e.printStackTrace();
        ResponseResult result = null;
        //用户不同的错误操作导致不同的异常类型，从而返回不同的错误信息
        if(e instanceof InsufficientAuthenticationException){
            //没有携带token进行访问就会触发InsufficientAuthenticationException，在这里设置返回数据
            result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }else if(e instanceof InternalAuthenticationServiceException){
            result = ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_ERROR,e.getMessage());
        }else{
            result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),"认证错误！");
        }
        //以JSON字符串的形式响应给前端
        WebUtils.renderString(httpServletResponse, JSON.toJSONString(result));
    }
}
