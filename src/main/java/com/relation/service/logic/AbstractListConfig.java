package com.relation.service.logic;

import com.relation.annotation.ListField;
import com.relation.common.util.SearchUtil;
import com.relation.enums.*;
import com.relation.resource.DefaultPageResource;
import com.relation.vo.ListConfigVo;
import com.relation.vo.SearchVo;
import com.relation.vo.list.ActionVo;
import com.relation.vo.list.FieldVo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListConfig implements ListConfig, Serializable {

    private ListConfigVo listConfig;

    /**
     * 带有数据表信息的class对象
     */
    private final Class<?> entityClass;

    @Autowired
    private ApplicationContext applicationContext;

    public AbstractListConfig(Class<?> entityClass)
    {
        this.entityClass = entityClass;
    }

    /**
     *初始化配置
     */
    protected void _init()
    {
        listConfig = new ListConfigVo();
        listConfig.setEditable(this.editable());
        listConfig.setApi(this.api());
        listConfig.setKye(this.keys());
        listConfig.setFields(this.fields());
        listConfig.setUnfold(this.unfold());
        listConfig.setActions(this.actions());
        listConfig.setBatchActions(this.batchActions());
    }

    /**
     * 刷新配置
     */
    protected void _refresh(ListConfigVo listConfig)
    {
        _refreshFieldList(listConfig.getFields());
    }

    /**
     * 刷新字段
     */
    protected void _refreshFieldList(List<FieldVo> fields)
    {

    }

    @Override
    public void refresh() {
        _refresh(listConfig);
    }

    @Override
    public TableType tableType()
    {
        return TableType.SIMPLE;
    }

    @Override
    public boolean editable() {
        return tableType().equals(TableType.EDITABLE);
    }

    @Override
    public List<FieldVo> fields()
    {
        List<FieldVo> fieldList = new ArrayList<>();
        pushEntityField(fieldList, entityClass);
        customFields(fieldList);
        return fieldList;
    }

    /**
     * 可过滤字段
     */
    @Override
    public List<String> filterableFields()
    {
        List<String> list = new ArrayList<>();
        for (FieldVo fieldVo:listConfig.getFields()
             ) {
            if(fieldVo.getIsFilterable()){
                list.add(fieldVo.getCode());
            }
        }
        return list;
    }

    /**
     * 添加实体属性
     */
    private void pushEntityField(List<FieldVo> fieldList, Class<?> entityClass){
        List<Field> fields = SearchUtil.getEntityFields(entityClass);
        for (Field field: fields
             ) {
            if(field.isAnnotationPresent(ListField.class)){
                fieldList.add(buildField(field));
            }
        }
    }

    protected FieldVo buildField(Field field)
    {
        ListField listField = field.getAnnotation(ListField.class);
        FieldVo fieldVo = createField();
        fieldVo.setLabel(listField.label());
        fieldVo.setCode(field.getName());
        fieldVo.setQueryCode(listField.queryCode());
        fieldVo.setIsVisible(listField.isVisible());
        fieldVo.setIsFilterable(listField.isFilterable());
        fieldVo.setIsSortable(listField.isSortable());
        fieldVo.setEditable(listField.editable());
        fieldVo.setConditionType(listField.conditionType());
        fieldVo.setValueType(listField.valueType());
        fieldVo.setInputType(listField.inputType());
        if(ValueType.LIST == fieldVo.getValueType()){
            Class<?> childrenEntity = listField.children();
            List<FieldVo> childrenFieldList = new ArrayList<>();
            pushEntityField(childrenFieldList, childrenEntity);
            fieldVo.setChildren(childrenFieldList);
        }
        if(! ObjectUtils.isEmpty(listField.options())){
            try {
                fieldVo.setOptions(applicationContext.getBean(listField.options()).getList());
            }catch (BeansException e){
                try {
                    fieldVo.setOptions(listField.options().newInstance().getList());
                } catch (InstantiationException | IllegalAccessException ignored) {}
            }
        }
        if(fieldVo.getOptions() == null){
            fieldVo.setOptions(new ArrayList<>());
        }
        return fieldVo;
    }

    /**
     * 关键词搜索字段
     */
    @Override
    public abstract List<String> keys();

    /**
     * 展开数据
     */
    public String unfold()
    {
        return "";
    }

    /**
     * 动态添加字段
     */
    protected void customFields(List<FieldVo> fieldList)
    {

    }

    @Override
    public String api(){
        return "search/list/" + code();
    }

    @Override
    public abstract String code();

    /**
     * 数据源
     *
     * @return
     */
    @Override
    public abstract DefaultPageResource<?> data(SearchVo searchVo);

    /**
     * 操作按钮
     */
    @Override
    public List<ActionVo> actions()
    {
        return new ArrayList<>();
    }

    /**
     * 批处理按钮
     */
    @Override
    public abstract List<ActionVo> batchActions();

    /**
     * 创建Field对象
     */
    protected FieldVo createField()
    {
        return new FieldVo();
    }

    /**
     * 获取列表配置
     */
    @Override
    public ListConfigVo getListConfig() {
        if(listConfig == null){
            _init();
        }
        return listConfig;
    }

    @Override
    public void setListConfig(ListConfigVo listConfig) {
        this.listConfig = listConfig;
    }

    /**
     * 获取主表class
     */
    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }
}
