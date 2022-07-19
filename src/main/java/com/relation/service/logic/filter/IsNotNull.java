package com.relation.service.logic.filter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.vo.search.FilterVo;

public class IsNotNull implements QueryFilter{

    @Override
    public void doFilter(QueryWrapper<?> queryWrapper, FilterVo filterVo) {
        queryWrapper.isNotNull(filterVo.getField());
    }

}
