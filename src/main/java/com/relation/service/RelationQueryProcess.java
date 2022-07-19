package com.relation.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public interface RelationQueryProcess {

    void query(QueryWrapper<?> queryWrapper, String relation_table);

}
