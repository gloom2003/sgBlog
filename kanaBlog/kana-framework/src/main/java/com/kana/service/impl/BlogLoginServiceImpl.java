package com.kana.service.impl;

import com.kana.constants.SystemConstants;
import com.kana.domain.ResponseResult;
import com.kana.domain.entity.LoginUser;
import com.kana.domain.entity.User;
import com.kana.domain.vo.BlogUserLoginVo;
import com.kana.domain.vo.UserInfoVo;
import com.kana.service.BlogLoginService;
import com.kana.utils.BeanCopyUtil;
import com.kana.utils.JwtUtil;
import com.kana.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;

    /***
     * 登录验证，登录成功则生成token并把用户信息存入Redis，最后返回token与用户信息
     * @param user
     * @return
     */
    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        //authenticationManager.authenticate方法执行流程图中的方法，
        // 自定义后查询数据库中用户信息并且与传入的信息进行加密对比，验证成功则写入Authentication对象返回，否则返回null
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authenticate)){
            //验证失败
            throw new RuntimeException("authenticationManager.authenticate验证出错：用户名或密码错误！");
        }
        //验证成功根据用户id生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String s_id = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(s_id);
        //根据用户id存储到Redis中
        redisCache.setCacheObject("bloglogin:"+s_id,loginUser);
        //封装vo user拷贝出userInfoVo对象并且封装为blogUserLoginVo对象
        UserInfoVo userInfoVo = BeanCopyUtil.beanCopy(loginUser.getUser(), UserInfoVo.class);
        BlogUserLoginVo blogUserLoginVo = new BlogUserLoginVo(jwt, userInfoVo);
        //封装为ResponseResult类返回
        return ResponseResult.okResult(blogUserLoginVo);
    }

    /***
     *  退出登录功能的实现, 需要请求头中必须携带token才能正常访问 为什么呢？？
     *  回答：因为没有携带token的话根本通过不了JwtAuthenticationTokenFilter过滤器，相当于没有登录，正常功能不能被访问，
     *  包括退出登录接口
     * @return
     */
    @Override
    public ResponseResult logout() {
        //获取userId并删除Redis中的用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        redisCache.deleteObject(SystemConstants.RECEPTION_REDIS_TOKEN_KEY_PREFIX +userId);
        return ResponseResult.okResult();
    }
}
