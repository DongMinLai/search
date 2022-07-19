package com.relation.service.logic.filter;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.common.util.SpringContextUtils;
import com.relation.resource.DefaultPageResource;
import com.relation.service.logic.ListConfig;
import com.relation.vo.SearchVo;
import com.relation.vo.search.FilterVo;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class Exists implements QueryFilter{

    @Override
    public void doFilter(QueryWrapper<?> queryWrapper, FilterVo filterVo) {
        JSONArray strArray = (JSONArray) filterVo.getValue();
        if(strArray.size() == 2){
            if(ObjectUtils.isEmpty(strArray.get(0))){
                return ;
            }
            ListConfig listConfig = getListConfig((String) strArray.get(0));
            DefaultPageResource<?> resource = listConfig.data(getSearchVo((String) strArray.get(1)));
            Set<Long> set = new HashSet<>();
            resource.result().getList().stream().filter(item->
                item.containsKey("id")
            ).forEach(item-> set.add((Long) item.get("id")) );
            if(set.isEmpty()){
                set.add(0L);
            }
            queryWrapper.in(filterVo.getField(), set.toArray());
        }
    }

    private ListConfig getListConfig(String key)
    {
        return SpringContextUtils.getBean(key, ListConfig.class);
    }

    private SearchVo getSearchVo(String value)
    {
        SearchVo searchVo = new SearchVo();
        searchVo.setKey(value);
        searchVo.setPageSize(-1);
        return searchVo;
    }

}
