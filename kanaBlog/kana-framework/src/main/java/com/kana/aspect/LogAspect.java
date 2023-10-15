package com.kana.aspect;

import com.alibaba.fastjson.JSON;
import com.kana.annotation.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 日志打印切面类
 */
@Component
@Aspect
@Slf4j
public class LogAspect {
    //指定切点(增强对象)
    @Pointcut("@annotation(com.kana.annotation.SystemLog)")
    public void pt(){

    }
    //使用环绕方法设置通知内容
    @Around("pt()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //处理前进行打印
        handleBefore(joinPoint);
        Object res = null;
        try {
            //执行原方法
            res = joinPoint.proceed();
            //处理后进行打印
            handleAfter(res);
        }finally{
            // 结束后换行
            log.info("=======End=======" + System.lineSeparator());
        }
        return res;
    }

    private void handleAfter(Object object) {
        // 打印出参
        log.info("Response       : {}", JSON.toJSONString(object));
    }

    private void handleBefore(ProceedingJoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String classPath = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        //获取http请求封装而成的对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取被增强方法的注解对象以及注解内部的信息
        SystemLog systemLog  = getAnnotation(signature);
        String businessName = systemLog.BusinessName();
        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}",request.getRequestURL());
        // 打印描述信息
        log.info("BusinessName   : {}",businessName);
        // 打印 Http method
        log.info("HTTP Method    : {}",request.getMethod() );
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}",classPath,methodName);
        // 打印请求的 IP
        log.info("IP             : {}",request.getRemoteHost());
        // 打印请求入参  把对象进行json的序列化之后进行打印
        log.info("Request Args   : {}", JSON.toJSONString(args));
    }

    private SystemLog getAnnotation(MethodSignature signature){
        return signature.getMethod().getAnnotation(SystemLog.class);
    }
}
