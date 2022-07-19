package com.relation.model.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import com.relation.common.util.SearchUtil;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

public class RelationResultHandler implements ResultHandler<Map<String, Object>>{

    private final Map<Object, List<Object>> relationData;

    private final Class<?> entityClass;

    private final String groupBy;

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
        //处理需要类型处理器的字段
        typeHandler(map);

        bean = BeanUtil.fillBeanWithMap(map, bean, true);

        if(map.containsKey(groupBy)){
            Object groupKey = map.get(groupBy);
            List<Object> group = relationData.getOrDefault(StrUtil.toString(groupKey), new ArrayList<>());
            group.add(bean);
            relationData.put(StrUtil.toString(groupKey), group);
        }
    }

    /**
     * 字段类型处理器
     * @return
     */
    private Map<String, Class<?>> typeHandler(Map<String, Object> map)
    {
        Map<String, Class<?>> handlerMap = new HashMap<>();
        SearchUtil.getEntityFields(entityClass).forEach(field -> {
            if(field.isAnnotationPresent(TableField.class)
                    && ! field.getAnnotation(TableField.class).typeHandler().equals(UnknownTypeHandler.class)){
                String key = SearchUtil.fieldFormat(field.getName(), "_");
                handlerMap.put(key, field.getAnnotation(TableField.class).typeHandler());
                Object value = map.getOrDefault(key, null);
                if(value != null){
                    map.put(key, JSON.parseObject(value.toString(), field.getType()));
                }
            }
        });
        return handlerMap;
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
            SearchUtil.setFieldValue(item, ResultField, relationData.getOrDefault(StrUtil.toString(groupKey), null));
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
            List<Object> values = relationData.getOrDefault(StrUtil.toString(groupKey), null);
            Object value = null;
            if(values != null && values.size() > 0){
                value = values.get(0);
            }
            SearchUtil.setFieldValue(item, ResultField, value);
        }
    }

    /**
     * 是否为空
     * @return
     */
    public Boolean isEmpty()
    {
        return CollectionUtils.isEmpty(relationData);
    }

    /**
     * 获取结果集
     * @return
     */
    public List<Object> getRelationDataList() {
        List<Object> list = new ArrayList<>();
        relationData.values().forEach(list::addAll);
        return list;
    }
}
