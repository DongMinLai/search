package com.relation.service.logic.relation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.annotation.HasOne;
import com.relation.common.util.SearchUtil;
import com.relation.exception.LoadRelationException;
import com.relation.mapper.RelationMapper;
import com.relation.model.handler.RelationResultHandler;
import com.relation.service.RelationQueryProcess;
import com.relation.vo.relation.HasOneVo;

import java.lang.reflect.Field;
import java.util.List;

public class HasOneRelation extends AbstractRelation<HasOne, HasOneVo>{

    public HasOneRelation(RelationMapper relationMapper, Class<?> mainEntity)
    {
        super(relationMapper, mainEntity);
    }

    @Override
    void executeMapperQuery(List<?> entityList, RelationQueryProcess relationQueryProcess, String relation) {
        HasOneVo param = getMapperParam(entityList, relation);
        if(isEmptyValue(param)) {
            return;
        }
        RelationResultHandler relationResultHandler = new RelationResultHandler(getAnnotation(relation).related(), getAnnotation(relation).foreignKey());
        QueryWrapper<?> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(param.getForeignKey(), param.getKeyValue());
        setQueryWrapper(queryWrapper, relationQueryProcess, relation);

        relationMapper.hasOne(param, relationResultHandler, queryWrapper);
        relationResultHandler.putOneResult(entityList, getField(relation), getGroupKey(relation));
    }

    @Override
    protected HasOneVo getMapperParam(List<?> entityList, String relation) {
        TableName tableName = getAnnotation(relation).related().getAnnotation(TableName.class);
        if(tableName == null){
            throw new LoadRelationException("关系实体没有@TableName注解");
        }
        HasOneVo hasOneVo = new HasOneVo();
        hasOneVo.setColumns("*");
        hasOneVo.setTableName(tableName.value());
        hasOneVo.setForeignKey(getAnnotation(relation).foreignKey());
        hasOneVo.setKeyValue(getGroupValue(entityList, relation));
        return hasOneVo;
    }

    @Override
    HasOne _getAnnotation(String relation) {
        return getField(relation).getAnnotation(HasOne.class);
    }

    @Override
    Field getGroupKey(String relation) {
        return getAnnotation(relation).primaryKey().isEmpty()
                ? SearchUtil.getEntityTableIdField(mainEntity)
                : SearchUtil.getDeclaredField(mainEntity, SearchUtil.camel(getAnnotation(relation).primaryKey()));
    }

    @Override
    Class<?> getRelationEntity(String relation) {
        return getAnnotation(relation).related();
    }

    @Override
    String buildSql(String relation) {
        HasOne hasOne = getAnnotation(relation);
        String mainTable = getMainTableName();
        String mainKey = SearchUtil.getEntityTableId(mainEntity);
        String relationTable = getRelationTableName(relation);
        return "SELECT 1 FROM " + relationTable +
                " WHERE " + mainTable + "." + (hasOne.primaryKey().isEmpty() ? mainKey : hasOne.primaryKey()) + " = " +
                relationTable + "." + hasOne.foreignKey();
    }
}
