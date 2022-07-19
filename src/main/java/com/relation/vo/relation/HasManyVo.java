package com.relation.vo.relation;

import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class HasManyVo extends RelationVo{

    /**
     * 查询列
     */
    private String columns;

    /**
     * 关联主键[主表]
     */
    private String primaryKey;

    /**
     * 关联外键[关联表]
     */
    private String foreignKey;
}
