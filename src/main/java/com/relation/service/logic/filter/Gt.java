package com.relation.service.logic.filter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.vo.search.FilterVo;

public class Gt implements QueryFilter{

    @Override
    public void doFilter(QueryWrapper<?> queryWrapper, FilterVo filterVo) {
        queryWrapper.gt(filterVo.getField(), filterVo.getValue());
    }

}
