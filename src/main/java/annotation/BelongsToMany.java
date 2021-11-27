package annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BelongsToMany {

    /**
     * 关系
     */
    Class<?> related();

    /**
     * 中间表
     */
    String middleTable() default "";

    /**
     * 关联主键[主表]
     */
    String primaryKey() default "";

    /**
     * 主表关系外键【中间表】
     */
    String foreignPivotKey() default "";

    /**
     * 关联表关系外键【中间表】
     */
    String relatedPivotKey() default "";

    /**
     * 关联主键[关联表]
     */
    String relatedPrimaryKey() default "";

}
