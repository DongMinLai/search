package com.relation.enums;

public enum InputType {
    /**
     *  文本
     */
    TEXT,

    /**
     * 列表
     */
    SELECT,

    /**
     * 日期格式
     */
    DATE,

    /**
     * 月份选择
     */
    MONTH,

    /**
     * 时间格式
     */
    DATETIME,

    /**
     * 布尔值（开关）
     */
    BOOLEAN,

    /**
     * 单选
     */
    RADIO,

    /**
     * 文本域
     */
    TEXTAREA,

    /**
     * 区间
     */
    BETWEEN,

    /**
     * 日期区间
     */
    BETWEEN_DATE,

    /**
     * 时间区间
     */
    BETWEEN_DATETIME,

    /**
     * 多选搜索
     */
    MULTI_SEARCH,

    /**
     * 下拉框加搜索
     */
    SELECT_TEXT,

    /**
     * 图片
     */
    MEDIA_IMAGE,

    /**
     * 组合表单【下拉+输入框】
     */
    SELECT_INPUT,
}
