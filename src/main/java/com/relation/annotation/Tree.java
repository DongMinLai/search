package com.relation.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tree {

    /**
     * 关系
     */
    Class<?> related();

    /**
     * 节点【父级ID字段】
     * @return
     */
    String node();

    /**
     * 深度
     * @return
     */
    int depth() default -1;

}
