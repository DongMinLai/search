package com.relation.vo;

import com.relation.enums.TableType;
import com.relation.vo.list.ActionVo;
import com.relation.vo.list.FieldVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 列表配置
 */
@Data
public class ListConfigVo implements Serializable {

    /**
     * 表格类型
     */
    private TableType type;

    /**
     * 数据接口地址
     */
    private String api;

    /**
     * 是否可编辑的
     */
    private boolean editable;

    /**
     * 关键字搜索
     */
    private List<String> kye;

    /**
     * 列表字段
     */
    private List<FieldVo> fields;

    /**
     * 展开数据
     */
    private String unfold;

    /**
     * 操作按钮
     */
    private List<ActionVo> actions;

    /**
     * 批处理
     */
    private List<ActionVo> batchActions;

    /**
     * 用户自定义显示
     */
    private List<String> userView;

}
