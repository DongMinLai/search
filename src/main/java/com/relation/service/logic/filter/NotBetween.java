package com.relation.service.logic.filter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.common.util.FilterUtil;
import com.relation.vo.search.FilterVo;

import java.util.Arrays;
import java.util.List;

public class NotBetween implements QueryFilter{

    @Override
    public void doFilter(QueryWrapper<?> queryWrapper, FilterVo filterVo) {
        if(filterVo.getValue() instanceof List<?>){
            List<?> list =  (List<?>) filterVo.getValue();
            if(list.size() == 2) {
                queryWrapper.notBetween(filterVo.getField(), list.get(0), FilterUtil.formatDateValue((String) list.get(1)));
            }
        }else if(filterVo.getValue() instanceof String){
            String string  = filterVo.getValue().toString();
            if(string.contains(",")){
                List<String> values = Arrays.asList(string.split(","));
                String end = FilterUtil.formatDateValue(values.get(1));
                queryWrapper.notBetween(filterVo.getField(), values.get(0), end);
            }
            else{
                queryWrapper.lt(filterVo.getField(), filterVo.getValue());
            }
        }
    }

}
