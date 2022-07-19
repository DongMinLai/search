package com.relation.service.logic.filter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.vo.search.FilterVo;

public interface QueryFilter {

    void doFilter(QueryWrapper<?> queryWrapper, FilterVo filterVo);

}
