package com.relation.service.logic.relation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.annotation.BelongTo;
import com.relation.common.util.SearchUtil;
import com.relation.exception.LoadRelationException;
import com.relation.mapper.RelationMapper;
import com.relation.model.handler.RelationResultHandler;
import com.relation.service.RelationQueryProcess;
import com.relation.vo.relation.BelongToVo;

import java.lang.reflect.Field;
import java.util.List;

public class BelongToRelation extends AbstractRelation<BelongTo, BelongToVo>{
    public BelongToRelation(RelationMapper relationMapper, Class<?> mainEntity)
    {
        super(relationMapper, mainEntity);
    }

    @Override
    void executeMapperQuery(List<?> entityList, RelationQueryProcess relationQueryProcess, String relation) {
        BelongToVo param = getMapperParam(entityList, relation);
        if(isEmptyValue(param)) {
            return;
        }
        RelationResultHandler relationResultHandler = new RelationResultHandler(getAnnotation(relation).related(), param.getRelatedPrimaryKey());
        QueryWrapper<?> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(param.getRelatedPrimaryKey(), param.getKeyValue());
        setQueryWrapper(queryWrapper, relationQueryProcess, relation);

        relationMapper.belongTo(param, relationResultHandler, queryWrapper);
        relationResultHandler.putOneResult(entityList, getField(relation), getGroupKey(relation));
    }

    @Override
    protected BelongToVo getMapperParam(List<?> entityList, String relation) {
        TableName tableName = getAnnotation(relation).related().getAnnotation(TableName.class);
        if(tableName == null){
            throw new LoadRelationException("关系实体没有@TableName注解");
        }
        String relationTable = tableName.value();
        BelongToVo belongToVo = new BelongToVo();
        belongToVo.setColumns("*");
        belongToVo.setTableName(relationTable);
        belongToVo.setForeignKey(getAnnotation(relation).foreignKey());
        belongToVo.setRelatedPrimaryKey(getAnnotation(relation).relatedPrimaryKey().isEmpty()
                ? SearchUtil.getEntityTableId(getAnnotation(relation).related())
                : getAnnotation(relation).relatedPrimaryKey());
        belongToVo.setKeyValue(getGroupValue(entityList, relation));
        return belongToVo;
    }

    @Override
    BelongTo _getAnnotation(String relation) {
        return getField(relation).getAnnotation(BelongTo.class);
    }

    @Override
    Field getGroupKey(String relation) {
        return SearchUtil.getDeclaredField(mainEntity, SearchUtil.camel(getAnnotation(relation).foreignKey()));
    }

    @Override
    Class<?> getRelationEntity(String relation) {
        return getAnnotation(relation).related();
    }

    @Override
    String buildSql(String relation) {
        BelongTo belongTo = getAnnotation(relation);
        String mainTableName = getMainTableName();
        String relationTableName = getRelationTableName(relation);
        String relationPrimaryKey = SearchUtil.getEntityTableId(getRelationEntity(relation));
        return "SELECT 1 FROM " + relationTableName +
                " WHERE " + mainTableName + "." + belongTo.foreignKey() + " = " + relationTableName + "." +
                (belongTo.relatedPrimaryKey().isEmpty() ? relationPrimaryKey :belongTo.relatedPrimaryKey());
    }
}
