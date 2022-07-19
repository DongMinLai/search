package com.relation.vo;

import lombok.Data;

import java.util.Collection;
import java.util.Set;

/**
 * @Author 丁廷宠 413778746@qq.com
 * @Describe
 * @Date： 2022/4/29 10:04
 * Copyright(c)2018-2021 Livolo All rights reserved.
 */
@Data
public class CustomConfigVo {
    private String config;
    /**
     * 自定义显示的列
     */
    private Collection<String> fields;

    /**
     * 自定义按钮显示
     */
    private Collection<String> actions;

    /**
     * 自定过滤
     */
}
