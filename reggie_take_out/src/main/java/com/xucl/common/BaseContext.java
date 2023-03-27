package com.xucl.common;

/**
 * @author xucl
 * @apiNote 基于ThreadLocal封装工具类，用于保存和获取当前登录用户的id 线程隔离
 * @date 2023/3/23 16:33
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    public static void setCurrentId(long id) {
        threadLocal.set(id);
    }

    public static long getCurrentId() {
        return threadLocal.get();
    }
}
