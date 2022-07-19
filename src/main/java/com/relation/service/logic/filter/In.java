package com.relation.service.logic.filter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.common.util.FilterUtil;
import com.relation.vo.search.FilterVo;

import java.util.Collection;

public class In implements QueryFilter{

    @Override
    public void doFilter(QueryWrapper<?> queryWrapper, FilterVo filterVo) {
        Collection<?> inValue = FilterUtil.getValueAsList(filterVo.getValue());
        filterVo.setValue(inValue);
        queryWrapper.in(filterVo.getField(), inValue);
    }

}
