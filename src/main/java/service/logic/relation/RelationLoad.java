package service.logic.relation;

import common.util.SearchUtil;
import exception.LoadRelationException;
import service.RelationQueryProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@Component
public class RelationLoad {

    @Autowired
    private RelationFactory relationFactory;

    /**
     * 加载数据关系
     * @param entity 数据实体
     * @param relation_str 关系
     * @param queryProcess 自定义条件
     */
    public void load(Object entity, String relation_str, RelationQueryProcess queryProcess)
    {
        if(entity == null){
            return;
        }
        List<Object> entityList = new ArrayList<>(1);
        entityList.add(entity);
        loadAll(entityList, relation_str, queryProcess);
    }

    /**
     * 加载数据关系
     * @param entity 数据实体
     * @param relation_str 关系
     */
    public void load(Object entity, String relation_str)
    {
        load(entity, relation_str, null);
    }

    /**
     * 加载数据关系
     * @param entity 数据实体
     * @param relation_strs 关系
     */
    public void load(Object entity, String... relation_strs)
    {
        if(entity == null){
            return;
        }
        List<Object> entityList = new ArrayList<>(1);
        entityList.add(entity);
        loadAll(entityList, relation_strs);
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
     * @param relation_str 关系
     * @param queryProcess 自定义条件
     */
    public void loadAll(List<?> entityList, String relation_str, RelationQueryProcess queryProcess)
    {
        if(entityList.isEmpty()){
            return;
        }
        Class<?> entity = entityList.get(0).getClass();
        if(relation_str.contains(".")){
            String[] searchFields = relation_str.split("\\.",2);
            try {
                List<Object> subEntityList = new ArrayList<>();
                for (Object group:SearchUtil.getFieldValues(entityList, entity.getDeclaredField(searchFields[0]))
                     ) {
                    Collection<?> list = (List<?>) group;
                    subEntityList.addAll(list);
                }
                loadAll(subEntityList, searchFields[1]);
            } catch (NoSuchFieldException e) {
                throw new LoadRelationException(searchFields[0] + "关系不存在");
            }
        }else{
            Relation relation = relationFactory.create(entity, relation_str);
            relation.loadAll(entityList, relation_str, queryProcess);
        }
    }

    /**
     * 加载数据关系
     * @param entityList 数据源
     * @param relation_str 关系字段
     */
    public void loadAll(List<?> entityList, String relation_str)
    {
        loadAll(entityList, relation_str, null);
    }

    /**
     * 加载数据关系
     * @param entityList 数据源
     * @param relation_strs 关系字段
     */
    public void loadAll(List<?> entityList, String... relation_strs)
    {
        Map<Integer, List<String>> levelGroup = setLevel(Arrays.stream(relation_strs).iterator());
        for (Integer level : levelGroup.keySet()) {
            List<String> re_strs = levelGroup.get(level);
            CountDownLatch cdl = new CountDownLatch(re_strs.size());
            for (String relation_str : re_strs
            ) {
                Thread t = new Thread(() -> {
                    try {
                        loadAll(entityList, relation_str, null);
                    }finally {
                        cdl.countDown();
                    }
                });
                t.start();
            }
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            List<String> relation_strs = levelGroup.get(level);
            CountDownLatch cdl = new CountDownLatch(relation_strs.size());
            for (String relation_str : relation_strs
            ) {
                Thread t = new Thread(() -> {
                    try {
                        loadAll(entityList, relation_str, relations.get(relation_str));
                    }finally {
                        cdl.countDown();
                    }
                });
                t.start();
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
