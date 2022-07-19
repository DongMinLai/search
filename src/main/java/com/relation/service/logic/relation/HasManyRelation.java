package com.relation.service.logic.relation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.annotation.HasMany;
import com.relation.common.util.SearchUtil;
import com.relation.exception.LoadRelationException;
import com.relation.mapper.RelationMapper;
import com.relation.model.handler.RelationResultHandler;
import com.relation.service.RelationQueryProcess;
import com.relation.vo.relation.HasManyVo;

import java.lang.reflect.Field;
import java.util.List;

public class HasManyRelation extends AbstractRelation<HasMany, HasManyVo>{

    public HasManyRelation(RelationMapper relationMapper, Class<?> mainEntity) {
        super(relationMapper, mainEntity);
    }

    @Override
    void executeMapperQuery(List<?> entityList, RelationQueryProcess relationQueryProcess, String relation) {
        HasManyVo param = getMapperParam(entityList, relation);
        if(isEmptyValue(param)) {
            return;
        }
        RelationResultHandler relationResultHandler = new RelationResultHandler(
        getAnnotation(relation).related(), getAnnotation(relation).foreignKey());
        QueryWrapper<?> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(param.getForeignKey(), param.getKeyValue());
        setQueryWrapper(queryWrapper, relationQueryProcess, relation);

        relationMapper.hasMany(param, relationResultHandler, queryWrapper);
        relationResultHandler.putResult(entityList, getField(relation), getGroupKey(relation));
    }

    @Override
    protected HasManyVo getMapperParam(List<?> entityList, String relation) {
        TableName tableName = getAnnotation(relation).related().getAnnotation(TableName.class);
        if(tableName == null){
            throw new LoadRelationException("关系实体没有@TableName注解");
        }
        HasManyVo hasManyVo = new HasManyVo();
        hasManyVo.setColumns("*");
        hasManyVo.setTableName(tableName.value());
        hasManyVo.setForeignKey(getAnnotation(relation).foreignKey());
        hasManyVo.setKeyValue(getGroupValue(entityList, relation));
        return hasManyVo;
    }

    @Override
    HasMany _getAnnotation(String relation) {
        return getField(relation).getAnnotation(HasMany.class);
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
        HasMany hasMany = getAnnotation(relation);
        String mainTable = getMainTableName();
        String mainKey = SearchUtil.getEntityTableId(mainEntity);
        String relationTable = getRelationTableName(relation);
        return "SELECT 1 FROM " + relationTable +
                " WHERE " + mainTable + "." + (hasMany.primaryKey().isEmpty() ? mainKey : hasMany.primaryKey()) + " = " +
                relationTable + "." + hasMany.foreignKey();
    }
}
