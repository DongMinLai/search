package annotation;

import enums.ConditionType;
import enums.InputType;
import enums.ValueType;
import service.logic.SelectOptions;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListField {

    /**
     * 标题
     */
    String label();

    /**
     * 字段编码
     */
    String code() default "";

    /**
     * 是否显示
     */
    boolean isVisible() default true;

    /**
     * 可过滤
     */
    boolean isFilterable() default true;

    /**
     * 可排序
     */
    boolean isSortable() default true;

    /**
     * 条件类型
     */
    ConditionType conditionType() default ConditionType.LIKE;

    /**
     * 内容类型
     */
    ValueType valueType() default ValueType.TEXT;

    /**
     * 输入类型
     */
    InputType inputType() default InputType.TEXT;

    /**
     * 子列表
     */
    Class<?> children() default Class.class;

    /**
     * 可选数据列表
     */
    Class<? extends SelectOptions> options() default SelectOptions.class;
    
}
