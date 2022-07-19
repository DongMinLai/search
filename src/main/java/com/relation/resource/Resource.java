package com.relation.resource;

import java.util.Map;

public interface Resource<T, R> {

    /**
     * 获取处理后的数据
     * @return
     */
    R result();

    /**
     * 修改某个属性
     * @param key
     * @param modifyValue
     * @return
     */
     void modifyValue(String key, ModifyValue modifyValue);

    /**
     * 修改某个属性
     * @param key
     * @param modifyValueByMap
     * @return
     */
    void modifyValue(String key, ModifyValueByMap modifyValueByMap);

    /**
     * 修改整行数据
     * @param modifyItem
     * @return
     */
    void modifyItem(ModifyItem modifyItem);

}
