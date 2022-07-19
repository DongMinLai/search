package com.relation.service.logic.relation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.annotation.Tree;
import com.relation.common.util.SearchUtil;
import com.relation.exception.LoadRelationException;
import com.relation.mapper.RelationMapper;
import com.relation.model.handler.RelationResultHandler;
import com.relation.service.RelationQueryProcess;
import com.relation.vo.relation.HasManyVo;
import com.relation.vo.relation.TreeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;

public class TreeRelation extends AbstractRelation<Tree, TreeVo>{

    public TreeRelation(RelationMapper relationMapper, Class<?> mainEntity) {
        super(relationMapper, mainEntity);
    }

    @Override
    protected TreeVo getMapperParam(List<?> entityList, String relation) {
        TableName tableName = getAnnotation(relation).related().getAnnotation(TableName.class);
        if(tableName == null){
            throw new LoadRelationException("关系实体没有@TableName注解");
        }
        TreeVo vo = new TreeVo();
        vo.setNodeKey(getAnnotation(relation).node());
        vo.setTableName(tableName.value());
        vo.setColumns("*");
        vo.setKeyValue(getGroupValue(entityList, relation));
        return vo;
    }

    @Override
    void executeMapperQuery(List<?> entityList, RelationQueryProcess relationQueryProcess, String relation) {
        //使用hasMany查询
        TreeVo treeVo = getMapperParam(entityList, relation);
        HasManyVo param = new HasManyVo();
        BeanUtils.copyProperties(treeVo, param);
        param.setForeignKey(treeVo.getNodeKey());
        RelationResultHandler relationResultHandler = new RelationResultHandler(
                getAnnotation(relation).related(), getAnnotation(relation).node());
        QueryWrapper<?> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(param.getForeignKey(), param.getKeyValue());
        setQueryWrapper(queryWrapper, relationQueryProcess, relation);

        relationMapper.hasMany(param, relationResultHandler, queryWrapper);
        relationResultHandler.putResult(entityList, getField(relation), getGroupKey(relation));
        List<Object> childrenList = relationResultHandler.getRelationDataList();
        if(! CollectionUtils.isEmpty(childrenList)){
            executeMapperQuery(childrenList, relationQueryProcess, relation);
        }
    }

    @Override
    Tree _getAnnotation(String relation) {
        return getField(relation).getAnnotation(Tree.class);
    }

    @Override
    Field getGroupKey(String relation) {
        return SearchUtil.getEntityTableIdField(mainEntity);
    }

    @Override
    Class<?> getRelationEntity(String relation) {
        return getAnnotation(relation).related();
    }

    @Override
    String buildSql(String relation) {
        throw new LoadRelationException("暂不支持树型结构查询");
    }
}
