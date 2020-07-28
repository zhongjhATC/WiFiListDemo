package com.zhongjh.wifilistdemo.utils;

import java.util.Collection;

/**
 * 集合操作工具类
 */
public class CollectionUtils {

    /**
     * 判断集合是否为null或者0个元素
     */
    public static boolean isNullOrEmpty(Collection c) {
        return null == c || c.isEmpty();
    }
}
