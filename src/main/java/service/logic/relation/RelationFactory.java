package service.logic.relation;

import exception.SearchException;
import annotation.BelongTo;
import annotation.BelongsToMany;
import annotation.HasMany;
import annotation.HasOne;
import common.util.SearchUtil;
import mapper.RelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


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
        }
        throw new SearchException(relation_str+"关系未定义");
    }

    public Field getRelationField(Class<?> entity, String relation_str) {

        Field field = SearchUtil.getDeclaredFieldSource(entity, relation_str);
        if(field == null){
            throw new SearchException(relation_str+"属性不存在");
        }
        return field;
    }
}
