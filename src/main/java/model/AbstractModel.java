package model;

import service.logic.relation.RelationLoad;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class AbstractModel<T> {

    protected final T entity;

    protected final Class<T> entityClass;

    @Autowired
    protected RelationLoad relationLoad;

    public AbstractModel(T entity) {
        this.entity = entity;
        this.entityClass = (Class<T>) entity.getClass();
    }

    /**
     * 加载关联关系
     * @param relations
     */
    public void load(String... relations)
    {
        Arrays.stream(relations).forEach((relation)->{
            relationLoad.load(entity, relation);
        });
    }

}
