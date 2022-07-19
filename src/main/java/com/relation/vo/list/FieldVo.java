package com.relation.vo.list;

import com.relation.enums.ConditionType;
import com.relation.enums.InputType;
import com.relation.enums.ValueType;
import com.relation.vo.SelectOptionVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class FieldVo {

    public FieldVo()
    {
        this.isVisible = true;
        this.isFilterable = true;
        this.isSortable = true;
        this.editable = false;
        this.conditionType = ConditionType.LIKE;
        this.valueType = ValueType.TEXT;
        this.inputType = InputType.TEXT;
    }

    /**
     * 标题
     */
    private String label;

    /**
     * 字段编码
     */
    private String code;

    /**
     * 查询编码
     */
    private String queryCode;

    /**
     * 是否显示
     */
    private Boolean isVisible;

    /**
     * 可过滤
     */
    private Boolean isFilterable;

    /**
     * 可排序
     */
    private Boolean isSortable;

    /**
     * 可编辑
     */
    private Boolean editable;

    /**
     * 条件类型
     */
    private ConditionType conditionType;

    /**
     * 内容类型
     */
    private ValueType valueType;
    /**
     * 输入类型
     */
    private InputType inputType;

    /**
     * 可选数据列表
     */
    private List<SelectOptionVo> options;

    /**
     * 子列表
     */
    private List<FieldVo> children;

}
