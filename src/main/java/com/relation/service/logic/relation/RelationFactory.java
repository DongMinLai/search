package com.relation.service.logic.relation;

import com.relation.annotation.*;
import com.relation.common.util.SearchUtil;
import com.relation.exception.LoadRelationException;
import com.relation.mapper.RelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


@Component
public class RelationFactory {

    @Autowired
    private RelationMapper relationMapper;

    public Relation create(Class<?> entity, String relation_str)
    {
        Field field = getRelationField(entity, relation_str);
        Relation relation = null;
        if (field.isAnnotationPresent(HasMany.class)) {
            relation = new HasManyRelation(relationMapper, entity);
        } else if (field.isAnnotationPresent(HasOne.class)) {
            relation = new HasOneRelation(relationMapper, entity);
        } else if (field.isAnnotationPresent(BelongTo.class)) {
            relation = new BelongToRelation(relationMapper, entity);
        } else if (field.isAnnotationPresent(BelongsToMany.class)) {
            relation = new BelongToManyRelation(relationMapper, entity);
        } else if (field.isAnnotationPresent(Tree.class)) {
            relation = new TreeRelation(relationMapper, entity);
        }
        return relation;
    }

    public Class<?> getRelationEntity(Class<?> entity, String relation_str)
    {
        Field field = getRelationField(entity, relation_str);
        if (field.isAnnotationPresent(HasMany.class)) {
            return field.getAnnotation(HasMany.class).related();
        } else if (field.isAnnotationPresent(HasOne.class)) {
            return field.getAnnotation(HasOne.class).related();
        } else if (field.isAnnotationPresent(BelongTo.class)) {
            return field.getAnnotation(BelongTo.class).related();
        } else if (field.isAnnotationPresent(BelongsToMany.class)) {
            return field.getAnnotation(BelongsToMany.class).related();
        } else if (field.isAnnotationPresent(Tree.class)) {
            return field.getAnnotation(Tree.class).related();
        }
        throw new LoadRelationException(relation_str+"关系未定义");
    }

    public Field getRelationField(Class<?> entity, String relation_str) {

        Field field = SearchUtil.getDeclaredFieldSource(entity, relation_str);
        if(field == null){
            throw new LoadRelationException(relation_str+"属性不存在");
        }
        return field;
    }
}
