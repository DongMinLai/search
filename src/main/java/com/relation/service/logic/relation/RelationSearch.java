package com.relation.service.logic.relation;

import com.relation.common.util.SearchUtil;
import com.relation.enums.ConditionType;
import com.relation.service.logic.Filter;
import com.relation.vo.search.FilterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 关系查询处理器
 */
@Component
public class RelationSearch {

    @Autowired
    private RelationFactory relationFactory;

    @Autowired
    private Filter filter;

    public String search(Class<?> mainEntity, FilterVo filterVo)
    {
        if(! SearchUtil.isRelationSearch(filterVo.getField())){
            return "";
        }
        String[] searchFields = filterVo.getField().split("\\.",2);
        Relation relation = relationFactory.create(mainEntity, searchFields[0]);
        Class<?> relationEntity = relationFactory.getRelationEntity(mainEntity, searchFields[0]);
        filterVo.setField(searchFields[1]);
        if(SearchUtil.isRelationSearch(filterVo.getField())){
            String sql = this.search(relationEntity, filterVo);
            if(! sql.isEmpty()){
                return relation.searchSql(searchFields[0], (queryWrapper, relation_table) ->
                    queryWrapper.exists(sql)
                );
            }
            return relation.searchSql(searchFields[0]);
        }else{
            return relation.searchSql(searchFields[0], (queryWrapper, relation_table) ->{
                if(SearchUtil.isTableField(relationEntity, filterVo.getField())){
                    filterVo.setField(relation_table +"."+ filterVo.getField());
                    filter.buildQuery(queryWrapper, filterVo);
                }
            });
        }

    }

    public String search(Class<?> mainEntity, String field, Object value, ConditionType conditionType)
    {
        FilterVo filterVo = new FilterVo(field, value, conditionType);
        return search(mainEntity, filterVo);

    }
}
