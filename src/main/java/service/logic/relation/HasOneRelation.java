package service.logic.relation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import annotation.HasMany;
import annotation.HasOne;
import common.util.SearchUtil;
import exception.LoadRelationException;
import exception.SearchException;
import mapper.RelationMapper;
import model.handler.RelationResultHandler;
import service.RelationQueryProcess;
import vo.relation.HasOneVo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HasOneRelation extends AbstractRelation<HasOne, HasOneVo>{

    public HasOneRelation(RelationMapper relationMapper, Class<?> mainEntity)
    {
        super(relationMapper, mainEntity);
    }

    @Override
    void executeMapperQuery(List<?> entityList, RelationQueryProcess relationQueryProcess, String relation) {
        RelationResultHandler relationResultHandler = new RelationResultHandler(getAnnotation(relation).related(), getAnnotation(relation).foreignKey());
        HasOneVo param = getMapperParam(entityList, relation);

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
            throw new SearchException("关系实体没有@TableName注解");
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
    List<Object> getGroupValue(List<?> entityList, String relation) {
        if(! getAnnotation(relation).primaryKey().isEmpty()){
            Field field = SearchUtil.getDeclaredFieldSource(mainEntity, SearchUtil.camel(getAnnotation(relation).primaryKey()));
            if(field == null){
                return new ArrayList<>();
            }
            return SearchUtil.getFieldValues(entityList, field);
        }else{
            return SearchUtil.getFieldValues(entityList, getGroupKey(relation));
        }
    }

    @Override
    Field getGroupKey(String relation) {
        try {
            if(! getAnnotation(relation).primaryKey().isEmpty()){
                Field field = SearchUtil.getDeclaredFieldSource(mainEntity, SearchUtil.camel(getAnnotation(relation).primaryKey()));
                if(field == null){
                    throw new LoadRelationException("无法获取主表主键或者关联键");
                }
                return field;
            }else{
                return SearchUtil.getEntityTableIdField(mainEntity);
            }
        } catch (NoSuchFieldException e) {
            throw new LoadRelationException("无法获取主表主键或者关联键");
        }
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
