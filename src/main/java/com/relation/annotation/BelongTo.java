package com.relation.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BelongTo {

    /**
     * 关系
     */
    Class<?> related();

    /**
     * 外键[主表关联键]
     */
    String foreignKey() default "";

    /**
     * 关联主键[关联表关联键]
     */
    String relatedPrimaryKey() default "";

}
