package model.handler;

import common.util.SearchUtil;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationResultHandler implements ResultHandler<Map<String, Object>> {

    Map<Object, List<Object>> relationData;

    Class<?> entityClass;

    String groupBy;

    public RelationResultHandler(Class<?> entityClass, String groupBy)
    {
        this.entityClass = entityClass;
        this.groupBy = groupBy;
        relationData = new HashMap<>();
    }

    @Override
    public void handleResult(ResultContext<? extends Map<String, Object>> resultContext) {
        Map<String, Object> map = resultContext.getResultObject();
        Object bean;
        try {
            bean = entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return ;
        }
        List<Object> group = null;
        Object groupKey = null;
        for (Field field:SearchUtil.getEntityFields(entityClass)
             ) {
            Object value = map.get(SearchUtil.fieldFormat(field.getName(), "_"));
            SearchUtil.setFieldValue(bean, field, value);
        }
        if(map.containsKey(groupBy)){
            groupKey = map.get(groupBy);
            if(relationData.containsKey(groupKey)){
                group = relationData.get(groupKey);
            }else{
                group = new ArrayList<>();
            }
        }
        if(group != null){
            group.add(bean);
        }
        relationData.put(groupKey, group);
    }

    public Map<Object, List<Object>> getResult()
    {
        return relationData;
    }

    /**
     * 对多数据
     * @param entityList
     * @param ResultField
     * @param groupKeyField
     */
    public void putResult(List<?> entityList, Field ResultField, Field groupKeyField)
    {
        for (Object item: entityList
             ) {
            Object groupKey = SearchUtil.getFieldValue(item, groupKeyField);
            SearchUtil.setFieldValue(item, ResultField, relationData.getOrDefault(groupKey, null));
        }
    }

    /**
     * 对一数据
     * @param entityList
     * @param ResultField
     * @param groupKeyField
     */
    public void putOneResult(List<?> entityList, Field ResultField, Field groupKeyField)
    {
        for (Object item: entityList
        ) {
            Object groupKey = SearchUtil.getFieldValue(item, groupKeyField);
            List<Object> values = relationData.getOrDefault(groupKey, null);
            Object value = null;
            if(values != null && values.size() > 0){
                value = values.get(0);
            }
            SearchUtil.setFieldValue(item, ResultField, value);
        }
    }
}
