package com.relation.service.logic.relation;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.annotation.BelongsToMany;
import com.relation.common.util.SearchUtil;
import com.relation.exception.LoadRelationException;
import com.relation.mapper.RelationMapper;
import com.relation.model.handler.RelationResultHandler;
import com.relation.service.RelationQueryProcess;
import com.relation.vo.relation.BelongToManyVo;

import java.lang.reflect.Field;
import java.util.List;

public class BelongToManyRelation extends AbstractRelation<BelongsToMany, BelongToManyVo>{
    public BelongToManyRelation(RelationMapper relationMapper, Class<?> mainEntity)
    {
        super(relationMapper, mainEntity);
    }

    @Override
    void executeMapperQuery(List<?> entityList, RelationQueryProcess relationQueryProcess, String relation) {
        BelongToManyVo param = getMapperParam(entityList, relation);
        if(isEmptyValue(param)) {
            return;
        }
        RelationResultHandler relationResultHandler = new RelationResultHandler(getAnnotation(relation).related(), param.getForeignPivotKey());
        QueryWrapper<?> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(param.getForeignPivotKey(), param.getKeyValue());
        setQueryWrapper(queryWrapper, relationQueryProcess, relation);

        relationMapper.belongToMany(param, relationResultHandler, queryWrapper);
        relationResultHandler.putResult(entityList, getField(relation), getGroupKey(relation));
    }

    @Override
    protected BelongToManyVo getMapperParam(List<?> entityList, String relation) {
        TableName tableName = getAnnotation(relation).related().getAnnotation(TableName.class);
        if(tableName == null){
            throw new LoadRelationException("关系实体没有@TableName注解");
        }
        String relationTable = tableName.value();
        BelongToManyVo belongToManyVo = new BelongToManyVo();
        belongToManyVo.setColumns("*");
        belongToManyVo.setTableName(relationTable);
        belongToManyVo.setMiddleTableName(getAnnotation(relation).middleTable());

        belongToManyVo.setPrimaryKey(
                getAnnotation(relation).primaryKey().isEmpty()
                        ? SearchUtil.fieldFormat(getGroupKey(relation).getName(), "_")
                        : getAnnotation(relation).primaryKey()
        );

        belongToManyVo.setRelatedPrimaryKey(
                getAnnotation(relation).relatedPrimaryKey().isEmpty()
                        ? SearchUtil.getEntityTableId(getAnnotation(relation).related())
                        :  getAnnotation(relation).relatedPrimaryKey()
        );

        belongToManyVo.setForeignPivotKey(getAnnotation(relation).foreignPivotKey());
        belongToManyVo.setRelatedPivotKey(getAnnotation(relation).relatedPivotKey());
        belongToManyVo.setKeyValue(getGroupValue(entityList, relation));
        return belongToManyVo;
    }

    @Override
    BelongsToMany _getAnnotation(String relation) {
        return getField(relation).getAnnotation(BelongsToMany.class);
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
        BelongsToMany belongsToMany = getAnnotation(relation);
        String mainTableName = getMainTableName();
        String mainPrimaryKey = SearchUtil.getEntityTableId(mainEntity);
        String relationPrimaryKey = getRelationMainKey(relation);
        String relationTableName = getRelationTableName(relation);
        String middleTableName = belongsToMany.middleTable();

        return "SELECT " + relationPrimaryKey + " FROM " + relationTableName + "," + middleTableName +
                " WHERE " + middleTableName + "." + belongsToMany.relatedPivotKey() + " = " + relationTableName + "." + relationPrimaryKey +
                " AND " + middleTableName + "." + belongsToMany.foreignPivotKey() + " = " + mainTableName + "." +
                (belongsToMany.primaryKey().isEmpty() ? mainPrimaryKey : belongsToMany.primaryKey());
    }

    /**
     * 获取关联主键
     */
    private String getRelationMainKey(String relation)
    {
        String relationMainKey = "id";
        if(getAnnotation(relation).relatedPrimaryKey() !=null){
            relationMainKey = getAnnotation(relation).relatedPrimaryKey();
        }else{
            Field[] fields = getRelationEntity(relation).getDeclaredFields();
            for (Field f: fields
            ) {
                if(f.isAnnotationPresent(TableId.class)){
                    relationMainKey = SearchUtil.fieldFormat(f.getName(), "_");
                }
            }
        }
        return relationMainKey;
    }
}
