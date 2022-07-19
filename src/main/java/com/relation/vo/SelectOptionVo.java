package com.relation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectOptionVo {

    /**
     * 标题
     */
    private String label;

    /**
     * 值
     */
    private Object value;

    /**
     * 是否可选
     */
    private Boolean disabled;

    /**
     * 子列表
     */
    private List<SelectOptionVo> optgroup;

}
