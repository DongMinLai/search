package com.relation.service.logic.relation;

import com.relation.annotation.Tree;
import com.relation.common.util.SearchUtil;
import com.relation.exception.LoadRelationException;
import com.relation.service.RelationQueryProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Component
public class RelationLoad {

    @Autowired
    private RelationFactory relationFactory;

    /**
     * 线程池
     */
    @Autowired
    private ExecutorService executorService;

    /**
     * 加载数据关系
     * @param entity 数据实体
     * @param relationStr 关系
     * @param queryProcess 自定义条件
     */
    public void load(Object entity, String relationStr, RelationQueryProcess queryProcess)
    {
        if(entity == null){
            return;
        }
        List<Object> entityList = new ArrayList<>(1);
        entityList.add(entity);
        loadAll(entityList, relationStr, queryProcess);
    }

    /**
     * 加载数据关系
     * @param entity 数据实体
     * @param relationStr 关系
     */
    public void load(Object entity, String relationStr)
    {
        load(entity, relationStr, null);
    }

    /**
     * 加载数据关系
     * @param entity 数据实体
     * @param relationStrs 关系
     */
    public void load(Object entity, String... relationStrs)
    {
        if(entity == null){
            return;
        }
        List<Object> entityList = new ArrayList<>(1);
        entityList.add(entity);
        loadAll(entityList, relationStrs);
    }

    /**
     * 加载数据关系
     * @param entity 数据实体
     * @param relations 关系+自定义条件集合
     */
    public void load(Object entity, Map<String, RelationQueryProcess> relations)
    {
        if(entity == null){
            return;
        }
        List<Object> entityList = new ArrayList<>(1);
        entityList.add(entity);
        loadAll(entityList, relations);
    }

    /**
     *  加载数据关系
     * @param entityList 数据源
     * @param relationStr 关系
     * @param queryProcess 自定义条件
     */
    public void loadAll(List<?> entityList, String relationStr, RelationQueryProcess queryProcess)
    {
        if(entityList.isEmpty()){
            return;
        }
        Class<?> entity = entityList.get(0).getClass();
        if(relationStr.contains(".")){
            String[] searchFields = relationStr.split("\\.",2);
            Field field = SearchUtil.getDeclaredFieldSource(entity, searchFields[0]);
            List<Object> subEntityList = getSubList(entityList, field);
            loadAll(subEntityList, searchFields[1], queryProcess);

        }else{
            Relation relation = relationFactory.create(entity, relationStr);
            relation.loadAll(entityList, relationStr, queryProcess);
        }
    }

    /**
     * 获取父级参数集合
     * @param entityList
     * @param parentField
     * @return
     */
    private List<Object> getSubList(List<?> entityList, Field parentField)
    {
        List<Object> subEntityList = new ArrayList<>();
        for (Object group : SearchUtil.getFieldValues(entityList, parentField)
        ) {
            if(ObjectUtils.isEmpty(group)){
                throw new LoadRelationException(entityList.get(0).getClass().getName()+ "." + parentField.getName() + "关系未定义");
            }
            if(group instanceof Collection){
                subEntityList.addAll((Collection) group);
            }else{
                subEntityList.add(group);
            }
        }
        // 树型结构需要递归获取所有需要加载的集合
        if(parentField.isAnnotationPresent(Tree.class) && ! CollectionUtils.isEmpty(subEntityList)){
            subEntityList.addAll(getSubList(subEntityList, parentField));
        }
        return subEntityList;
    }

    /**
     * 加载数据关系
     * @param entityList 数据源
     * @param relationStr 关系字段
     */
    public void loadAll(List<?> entityList, String relationStr)
    {
        loadAll(entityList, relationStr, null);
    }

    /**
     * 加载数据关系
     * @param entityList 数据源
     * @param relationStrs 关系字段
     */
    public void loadAll(List<?> entityList, String... relationStrs)
    {
        Map<String, RelationQueryProcess> relations = new HashMap<String, RelationQueryProcess>(){{
            Arrays.stream(relationStrs).forEach(relation->  put(relation,null));
        }};
        loadAll(entityList, relations);
    }

    /**
     * 加载关系数据
     * @param entityList 数据源
     * @param relations 关系+自定义条件集合
     */
    public void loadAll(List<?> entityList, Map<String, RelationQueryProcess> relations)
    {
        Map<Integer, List<String>> levelGroup = setLevel(relations.keySet().iterator());
        for (Integer level : levelGroup.keySet()) {
            List<String> relationStrs = levelGroup.get(level);
            CountDownLatch cdl = new CountDownLatch(relationStrs.size());
            for (String relationStr : relationStrs
            ) {
                executorService.execute(() -> {
                    try {
                        loadAll(entityList, relationStr, relations.get(relationStr));
                    }finally {
                        cdl.countDown();
                    }
                });
            }
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取有序集合
     * @param relations 关系迭代器
     * @return 有序集合
     */
    public Map<Integer, List<String>> setLevel(Iterator<String> relations)
    {
        Map<Integer, List<String>> group = new TreeMap<>();
        while (relations.hasNext()){
            String relation = relations.next();
            int level = relation.split("\\.").length - 1;
            if(! group.containsKey(level)){
                group.put(level, new ArrayList<>());
            }
            group.get(level).add(relation);
        }
         return group;
    }

}
