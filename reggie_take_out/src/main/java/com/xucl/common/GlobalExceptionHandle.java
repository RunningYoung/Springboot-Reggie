package com.xucl.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author xucl
 * @apiNote 全局异常处理
 * @date 2023/3/23 10:32
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandle {
    /**
     * TODO
     *  异常处理方法
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException e){
        log.info(e.getMessage());

        if (e.getMessage().contains("Duplicate entry")) {
            String[] split = e.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> customException(CustomException e){
        log.info(e.getMessage());
        return R.error(e.getMessage());
    }
}
