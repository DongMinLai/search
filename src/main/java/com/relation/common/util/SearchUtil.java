package com.relation.common.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.relation.exception.LoadRelationException;
import org.apache.commons.beanutils.ConvertUtils;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchUtil {

    /**
     * 格式化参数
     */
    public static String fieldFormat(String field, String sp)
    {
        return field.replaceAll("([a-z])([A-Z])", "$1"+ sp +"$2").toLowerCase();
    }

    /**
     * 下划线转驼峰
     * @param str
     * @return
     */
    public static String camel(String str) {
        Pattern pattern = Pattern.compile("_(\\w)");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if(matcher.find()) {
            sb = new StringBuffer();
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            matcher.appendTail(sb);
        }else {
            return sb.toString();
        }
        return camel(sb.toString());
    }

    /**
     * 获取实体表
     */
    public static String getEntityTable(Class<?> entity)
    {
        while (entity != null && ! entity.getName().equals(Object.class.getName())) {
            if(entity.getAnnotation(TableName.class) != null){
                return entity.getAnnotation(TableName.class).value();
            }
            entity = entity.getSuperclass();  // 获得父类的字节码对象
        }
        return "";
    }

    /**
     * 判断是否为关联查询
     */
    public static boolean isRelationSearch(String field)
    {
        return field.contains(".");
    }

    /**
     * 获取表主键
     */
    public static String getEntityTableId(Class<?> entity)
    {
        String tableId = "id";
        for (Field field: getEntityFields(entity)
        ) {
            if(field.isAnnotationPresent(TableId.class)){
                return SearchUtil.fieldFormat(field.getName(), "_");
            }
        }
        return tableId;
    }

    /**
     * 获取表主键Field
     */
    public static Field getEntityTableIdField(Class<?> entity) {
        for (Field field: getEntityFields(entity)
        ) {
            if(field.isAnnotationPresent(TableId.class)){
                return field;
            }
        }
        try {
            return entity.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            throw new LoadRelationException(entity.getSimpleName()+"没有设置主键");
        }
    }

    /**
     * 判断是否是实体属性
     */
    public static boolean isTableField(Class<?> entity, String field)
    {
        while (entity != null && ! entity.getName().equals(Object.class.getName())) {
            try {
                Field f = entity.getDeclaredField(field);
                if(f.isAnnotationPresent(TableField.class)){
                    TableField tableField = f.getAnnotation(TableField.class);
                    return tableField.exist();
                }
                return true;
            } catch (NoSuchFieldException e) {
                entity = entity.getSuperclass();  // 获得父类的字节码对象
            }
        }
        return false;
    }
    /**
     * 获取实体属性
     */
    public static Field getDeclaredFieldSource(Class<?> entity, String field)
    {
        while (entity != null) {
            try {
                return entity.getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                // 获得父类的字节码对象
                entity = entity.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取实体属性
     */
    public static Field getDeclaredField(Class<?> entity, String field) {
        while (entity != null) {
            try {
                return entity.getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                // 获得父类的字节码对象
                entity = entity.getSuperclass();
            }
        }
        throw new LoadRelationException(field + "属性不存在");
    }


    /**
     * 获取类及其父类属性
     */
    public static List<Field> getEntityFields(Class<?> entity)
    {
        List<Field> fieldsList = new ArrayList<>();
        while (entity != null && ! entity.getName().equals(Object.class.getName())) {  // 遍历所有父类字节码对象
            Field[] declaredFields2 = entity.getDeclaredFields();
            fieldsList.addAll(Arrays.asList(declaredFields2));  //将`Filed[]`数组转换为`List<>`然后再将其拼接至`ArrayList`上
            entity = entity.getSuperclass();  // 获得父类的字节码对象
        }
        return fieldsList;
    }

    /**
     * 获取实体的get方法
     * @param entity
     * @return
     */
    public static List<Method> getEntityGetMethods(Class<?> entity)
    {
        List<Method> methods = new ArrayList<>();
        for (Method method: entity.getMethods()
        ) {
            if(method.getName().contains("get") && !method.getName().equals("getClass")){
                methods.add(method);
            }
        }
        return methods;
    }

    /**
     * 获取方法对应属性
     * @param method
     * @return
     */
    public static String getFieldNameByMethod(String method)
    {
        return method.substring(3,4).toLowerCase() + method.substring(4);
    }

    /**
     * 获取属性GET方法
     * @param field
     * @return
     */
    public static String getMethodNameByField(String field)
    {
        return "get"+field.substring(0,1).toUpperCase() + field.substring(1);
    }

    /**
     * 获取属性SET方法
     * @param field
     * @return
     */
    public static String setMethodNameByField(String field)
    {
        return "set"+field.substring(0,1).toUpperCase() + field.substring(1);
    }

    /**
     * 获取属性值
     * @param entity
     * @param field
     * @return
     */
    public static Object getFieldValue(Object entity, Field field)
    {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            try {
                Method method = entity.getClass().getMethod(SearchUtil.getMethodNameByField(field.getName()));
                return method.invoke(entity);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                return null;
            }
        }
    }

    /**
     * 批量获取属性值
     * @param entities
     * @param field
     * @return
     */
    public static List<Object> getFieldValues(Collection<?> entities, Field field)
    {
        List<Object> values = new ArrayList<>();
        for (Object entity: entities) {
            Object value = SearchUtil.getFieldValue(entity, field);
            if(value != null){
                values.add(value);
            }
        }
        return values;
    }

    /**
     * 设置对象
     * @param entity
     * @param field
     * @param value
     */
    public static void setFieldValue(Object entity, Field field, Object value)
    {
        if(value == null){
            return;
        }
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            try {
                Method method = entity.getClass().getMethod(SearchUtil.setMethodNameByField(field.getName()),field.getType());
                method.invoke(entity, ConvertUtils.convert(value, field.getType()));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            } catch (IllegalArgumentException ille){
                throw new LoadRelationException(entity.getClass()+"."+field.getName() + "结果类型设置错误,声明类型【"+field.getType()+"】,实际结果类型【"+value.getClass()+"】");
            }

        }
    }

}
