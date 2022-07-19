package com.relation.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasOne {

    /**
     * 关系
     */
    Class<?> related();

    /**
     * 主键[主表关联键]
     */
    String primaryKey() default "";

    /**
     * 外键[关联表关联键]
     */
    String foreignKey() default "";


}
