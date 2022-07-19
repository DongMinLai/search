package com.relation.vo.search;

import com.relation.enums.ConditionType;
import lombok.Data;

@Data
public class FilterVo {

    public FilterVo(){}

    public FilterVo(String field, Object value, ConditionType conditionType)
    {
        this.field = field;
        this.value = value;
        this.conditionType = conditionType;
    }

    /**
     * 字段名
     */
    private String field;

    /**
     * 值
     */
    private Object value;

    /**
     * 条件类型
     */
    private ConditionType conditionType;


}
