package service.logic.relation;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import annotation.BelongTo;
import annotation.BelongsToMany;
import annotation.HasMany;
import annotation.HasOne;
import common.util.SearchUtil;
import enums.ConditionType;
import service.logic.Filter;
import vo.search.FilterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String search(Class<?> mainEntity, String relation, Object value, ConditionType conditionType)
    {
        FilterVo filterVo = new FilterVo(relation, value, conditionType);
        return search(mainEntity, filterVo);

    }
}
