package com.kana.filter;

import com.alibaba.fastjson.JSON;
import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.entity.LoginUser;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.utils.JwtUtil;
import com.kana.utils.RedisCache;
import com.kana.utils.WebUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/***
 * 继承OncePerRequestFilter重写方法实现自定义过滤器至SpringSecurity中
 * redisCache.getCacheObject("bloglogin:" + userId)中的key不是公共的不能写在kana-framework中
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;

    /***
     * 前端携带token发起请求时后端的验证流程
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        //获取前端请求头中携带的token并解析出userId
        String token = httpServletRequest.getHeader("token");
        //检测是否含有token
        if(!StringUtils.hasText(token)){
            //没有则说明此接口不需要登录,直接放行并return
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        String userId = "";
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            //出现异常进入这里说明 token失效 或 token被篡改
            //返回响应给前端告诉他需要重新登录并return
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(httpServletResponse, JSON.toJSONString(result));
            return;
        }
        //从Redis中查询用户信息
        LoginUser loginUser = redisCache.getCacheObject(SystemConstants.RECEPTION_REDIS_TOKEN_KEY_PREFIX + userId);
        if(Objects.isNull(loginUser)){
            //Redis查询不到信息，过期了
            //返回响应给前端告诉他需要重新登录并return
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(httpServletResponse, JSON.toJSONString(result));
            return;
        }
        //把用户信息存储到SecurityContextHolder的authentication中
        //这里需要使用3个参数的重载形式才有super.setAuthenticated(true); 设置已经完成了认证
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(loginUser,null,null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(httpServletRequest,httpServletResponse);

    }
}
