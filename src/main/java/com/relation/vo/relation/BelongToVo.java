package com.relation.vo.relation;

import lombok.Data;

import java.util.List;

@Data
public class BelongToVo extends RelationVo{

    /**
     * 查询列
     */
    private String columns;

    /**
     * 关联主键[关联表]
     */
    private String relatedPrimaryKey;

    /**
     * 关联外键[主表]
     */
    private String foreignKey;

}
