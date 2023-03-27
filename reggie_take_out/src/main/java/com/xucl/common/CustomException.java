package com.xucl.common;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/23 18:00
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
