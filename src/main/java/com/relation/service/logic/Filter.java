package com.relation.service.logic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.common.util.SearchUtil;
import com.relation.vo.search.FilterVo;
import com.relation.vo.search.SortOrderVo;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Filter {

    /**
     * 处理搜索条件
     */
    public void buildQuery(QueryWrapper<?> queryWrapper, FilterVo filterVo)
    {
        if(filterVo.getValue() == null || "".equals(filterVo.getValue()))
        {
            return ;
        }
        filterVo.setField(SearchUtil.fieldFormat(filterVo.getField(), "_"));
        filterVo.getConditionType().getQueryFilter().doFilter(queryWrapper, filterVo);
    }


    /**
     * 处理排序
     */
    public void buildSortOrder(QueryWrapper<?> queryWrapper, SortOrderVo orderVo)
    {
        queryWrapper.orderBy(true, "ASC".equals(orderVo.getDirection().toUpperCase(Locale.ROOT)),
                orderVo.getField());
    }
}
