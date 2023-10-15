package com.kana.hander.exception;

import com.kana.domain.ResponseResult;
import com.kana.enums.AppHttpCodeEnum;
import com.kana.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/***
 * 全局异常处理器Controller,统一处理各种各样的异常
 */
//@RestBody与@ControllerAdvice的合体
@Slf4j
@RestControllerAdvice
//酸辣粉4j，lombok的日志打印
public class GlobalExceptionHander{
    /***
     * 专门处理SystemException异常，把异常作为参数传入，处理完毕后返回ResponseResult对象给前端
     * @param e
     * @return
     */
    @ExceptionHandler(SystemException.class)
    public ResponseResult systemExceptionHandler(SystemException e){
        //打印异常信息 使用{}作为占位符传入异常e打印异常信息 虽然报红但是还可以使用
        log.error("出现了异常!{}",e);
        return ResponseResult.errorResult(e.getCode(),e.getMsg());
    }

    /***
     * 处理其他异常? Exception,设置返回给前端的数据
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult exceptionHandler(Exception e){
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),e.getMessage());
    }
}
