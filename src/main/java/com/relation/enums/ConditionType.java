package com.relation.enums;

import com.relation.service.logic.filter.*;

/**
 * 可用条件类型
 */
public enum ConditionType {
    IN("IN", new In()),
    NOT_IN("NOT IN", new NotIn()),
    LIKE("LIKE", new Like()),
    LIKE_LEFT("LIKE LEFT", new LikeLeft()),
    LIKE_RIGHT("LIKE RIGHT", new LikeRight()),
    EQ("=", new Eq()),
    NE("<>", new Ne()),
    GT(">", new Gt()),
    GE(">=", new Ge()),
    LT("<", new Lt()),
    LE("<=", new Le()),
    IS_NULL("IS NULL", new IsNull()),
    IS_NOT_NULL("IS NOT NULL", new IsNotNull()),
    EXISTS("EXISTS", new Exists()),
    BETWEEN("BETWEEN", new Between()),
    NOT_BETWEEN("NOT BETWEEN", new NotBetween());

    private final String keyword;

    private final QueryFilter queryFilter;

    ConditionType(final String keyword, QueryFilter queryFilter) {
        this.keyword = keyword;
        this.queryFilter = queryFilter;
    }

    public String getKeyword()
    {
        return this.keyword;
    }

    public QueryFilter getQueryFilter() {
        return queryFilter;
    }
}
