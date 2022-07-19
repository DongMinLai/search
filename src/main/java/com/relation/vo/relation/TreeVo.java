package com.relation.vo.relation;

import lombok.Data;

@Data
public class TreeVo extends RelationVo{

    /**
     * 节点【父级ID字段】
     */
    private String nodeKey;

    /**
     * 查询列
     */
    private String columns;

}
