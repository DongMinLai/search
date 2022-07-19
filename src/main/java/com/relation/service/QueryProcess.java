package com.relation.service;

import com.relation.vo.QueryProcessVo;
import com.relation.vo.SearchVo;

import java.util.Collection;

public interface QueryProcess<T> {
    QueryProcessVo<T> execute(SearchVo searchVo, String config);

    QueryProcessVo<T> execute(SearchVo searchVo, Class<?> entityClass, Collection<String> keys);

    QueryProcessVo<T> execute(SearchVo searchVo, Class<?> entityClass);
}
