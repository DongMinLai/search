package com.relation.service.logic;

import com.relation.enums.TableType;
import com.relation.resource.DefaultPageResource;
import com.relation.resource.Resource;
import com.relation.vo.ListConfigVo;
import com.relation.vo.SearchVo;
import com.relation.vo.list.ActionVo;
import com.relation.vo.list.FieldVo;
import java.util.List;

public interface ListConfig {

    /**
     * 配置对象
     */
    ListConfigVo getListConfig();

    /**

     * 刷新列表配置
     */
    void refresh();

    /**
     * 表格类型
     */
    TableType tableType();


    void setListConfig(ListConfigVo listConfig);

    /**
     * 可编辑
     */
    boolean editable();

    /**
     * 数据接口
     */
    String api();

    /**
     * 数据源
     */
    DefaultPageResource<?> data(SearchVo searchVo);

    /**
     * 关键字
     */
    List<String> keys();

    /**
     * 列表KEY
     */
    String code();

    /**
     * 操作按钮
     */
    List<ActionVo> actions();

    /**
     * 批处理按钮
     */
    List<ActionVo> batchActions();

    /**
     * 可用字段列表
     */
    List<FieldVo> fields();

    /**
     * 主表实体
     */
    Class<?> getEntityClass();

    /**
     * 可过滤字段
     */
    List<String> filterableFields();
}
