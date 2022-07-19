package com.relation.service.logic.relation;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.relation.common.util.SearchUtil;
import com.relation.exception.LoadRelationException;
import com.relation.mapper.RelationMapper;
import com.relation.service.RelationQueryProcess;
import com.relation.vo.relation.RelationVo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRelation<T, R extends RelationVo> implements Relation{

    /**
     * 数据库资源类
     */
    protected RelationMapper relationMapper;

    /**
     * 关联注解
     */
    protected Map<String, T> annotations;

    /**
     *  关联字段
     */
    protected Map<String, Field> fields;

    /**
     * 主体
     */
    protected Class<?> mainEntity;

    public AbstractRelation(RelationMapper relationMapper, Class<?> mainEntity)
    {
        this.relationMapper = relationMapper;
        this.mainEntity = mainEntity;
        annotations = new HashMap<>();
        fields = new HashMap<>();
    }

    @Override
    public void loadAll(List<?> entityList, String relation) {
        loadAll(entityList, relation, null);
    }

    @Override
    public void loadAll(List<?> entityList, String relation, RelationQueryProcess relationQueryProcess) {
        if(entityList.isEmpty()){
            return;
        }
        executeMapperQuery(entityList, relationQueryProcess, relation);
    }

    @Override
    public String searchSql(String relation, RelationQueryProcess relationQueryProcess)
    {
        String applySql = buildSql(relation);
        return getFinalSql(relation, applySql, relationQueryProcess);
    }

    @Override
    public String searchSql(String relation)
    {
        return  searchSql(relation, null);
    }

    /**
     * 注入自定义条件
     * @param queryWrapper 条件对象
     * @param relationQueryProcess 自定义条件处理接口
     * @param relation 关系
     */
    protected void setQueryWrapper(QueryWrapper<?> queryWrapper, RelationQueryProcess relationQueryProcess, String relation)
    {
        if(relationQueryProcess != null){
            relationQueryProcess.query(queryWrapper, getRelationTableName(relation));
        }
        Class<?> relationEntity = getRelationEntity(relation);
        for (Field field: SearchUtil.getEntityFields(relationEntity)
             ) {
            if(field.isAnnotationPresent(TableLogic.class)){
                queryWrapper.eq(SearchUtil.fieldFormat(getRelationTableName(relation) + "." + field.getName(), "_"), 1);
            }
        }
    }

    /**
     * 获取关联属性
     * @param relation
     * @return
     */
    protected Field getField(String relation)
    {
        if(! fields.containsKey(relation)){
            Field field = SearchUtil.getDeclaredFieldSource(mainEntity, relation);
            if(field == null){
                throw new LoadRelationException(relation+"关系未定义");
            }
            fields.put(relation, field);
        }
        return fields.get(relation);
    }

    /**
     * 获取关联注解
     * @param relation
     * @return
     */
    protected T getAnnotation(String relation)
    {
        if(! annotations.containsKey(relation)){
            annotations.put(relation, _getAnnotation(relation));
        }
        return annotations.get(relation);
    }

    /**
     * 获取主表名
     * @return
     */
    protected String getMainTableName()
    {
        Class<?> entity = mainEntity;
        while (entity != null) {
            if(entity.isAnnotationPresent(TableName.class)){
                return entity.getAnnotation(TableName.class).value();
            }
            entity = entity.getSuperclass();
        }
        throw new LoadRelationException(mainEntity.getSimpleName() + "未找到@TableName注解");
    }

    /**
     * 获取关系表名
     * @param relation
     * @return
     */
    protected String getRelationTableName(String relation)
    {
        Class<?> entity = getRelationEntity(relation);
        while (entity != null) {
            if(getRelationEntity(relation).isAnnotationPresent(TableName.class)){
                return entity.getAnnotation(TableName.class).value();
            }
            entity = entity.getSuperclass();
        }
        throw new LoadRelationException(getRelationEntity(relation).getSimpleName() + "未找到@TableName注解");
    }

    /**
     * 获取最终查询SQL
     */
    private String getFinalSql(String relation, String applySql, RelationQueryProcess relationQueryProcess)
    {
        QueryWrapper<?> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply(applySql);
        setQueryWrapper(queryWrapper, relationQueryProcess, relation);

        return getWrapperSql(queryWrapper);
    }

    /**
     * 处理查询参数
     */
    private String getWrapperSql(QueryWrapper<?> queryWrapper)
    {
        String sql = queryWrapper.getSqlSegment();
        Map<String, Object> params = queryWrapper.getParamNameValuePairs();
        String pattern = "(#\\{ew.paramNameValuePairs.MPGENVAL\\d+})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(sql);
        while (m.find()) {
            String[] group = m.group().split(",");
            for (String mp: group
            ) {
                String target = mp.substring(25, mp.length() - 1);//去掉花括号\
                sql = sql.replace(mp, "\"" + params.get(target).toString() + "\"");
            }
        }
        return sql;
    }

    protected boolean isEmptyValue(RelationVo relationVo)
    {
        return (relationVo.getKeyValue() == null || relationVo.getKeyValue().size() == 0);
    }

    /**
     * 构建参数
     * @param entityList
     * @param relation
     * @return
     */
    protected abstract R getMapperParam(List<?> entityList, String relation);

    /**
     * 查询数据
     * @param entityList 数据源
     * @param relationQueryProcess 自定义条件处理接口
     * @param relation 关系
     */
    abstract void executeMapperQuery(List<?> entityList, RelationQueryProcess relationQueryProcess, String relation);

    /**
     * 获取注解
     * @param relation 关系
     * @return T
     */
    abstract T _getAnnotation(String relation);

    /**
     * 获取关联条件值
     * @param entityList 数据源
     * @param relation 关系
     * @return 查询数条件数据
     */
    protected List<Object> getGroupValue(List<?> entityList, String relation) {
        return SearchUtil.getFieldValues(entityList, getGroupKey(relation));
    }

    /**
     * 获取关联条件字段
     * @param relation 关系
     * @return 字段
     */
    abstract Field getGroupKey(String relation);

    /**
     * 获取关联实体
     * @param relation 关系
     * @return class
     */
    abstract Class<?> getRelationEntity(String relation);

    /**
     * 构建查询SQL
     * @param relation 关系
     * @return SQL
     */
    abstract String buildSql(String relation);

}
