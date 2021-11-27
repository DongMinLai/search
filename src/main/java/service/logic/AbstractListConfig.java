package service.logic;

import annotation.ListField;
import common.util.SearchUtil;
import enums.InputType;
import enums.ValueType;
import vo.ListConfigVo;
import vo.list.ActionVo;
import vo.list.FieldVo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListConfig implements ListConfig {

    private final ListConfigVo listConfig = new ListConfigVo();

    /**
     * 带有数据表信息的class对象
     */
    private final Class<?> entityClass;

    public AbstractListConfig(Class<?> entityClass)
    {
        this.entityClass = entityClass;
        this._init();
    }

    protected void _init()
    {
        listConfig.setApi(this.api());
        listConfig.setKye(this.keys());
        listConfig.setFields(this.fields());
        listConfig.setUnfold(this.unfold());
        listConfig.setActions(this.actions());
        listConfig.setBatchActions(this.batchActions());
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
                ListField listField = field.getAnnotation(ListField.class);
                FieldVo fieldVo = createField();
                fieldVo.setLabel(listField.label());
                fieldVo.setCode(field.getName());
                fieldVo.setIsVisible(listField.isVisible());
                fieldVo.setIsFilterable(listField.isFilterable());
                fieldVo.setIsSortable(listField.isSortable());
                fieldVo.setConditionType(listField.conditionType());
                fieldVo.setValueType(listField.valueType());
                fieldVo.setInputType(listField.inputType());
                if(ValueType.LIST == fieldVo.getValueType()){
                    Class<?> childrenEntity = listField.children();
                    List<FieldVo> childrenFieldList = new ArrayList<>();
                    pushEntityField(childrenFieldList, childrenEntity);
                    fieldVo.setChildren(childrenFieldList);

                }
                if(InputType.SELECT == fieldVo.getInputType()){
                    try {
                        fieldVo.setOptions(listField.options().newInstance().getList());
                    } catch (InstantiationException | IllegalAccessException ignored) {}
                }
                if(fieldVo.getOptions() == null){
                    fieldVo.setOptions(new ArrayList<>());
                }
                fieldList.add(fieldVo);
            }
        }
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
    public abstract String api();

    /**
     * 操作按钮
     */
    @Override
    public abstract List<ActionVo> actions();

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
        return listConfig;
    }

    /**
     * 获取主表class
     */
    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }
}
