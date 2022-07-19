package com.relation.resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultListResource<T> extends AbstractResult<T, List<LinkedHashMap<String, Object>>>{

    protected List<LinkedHashMap<String, Object>> result;

    protected List<T> originData;

    public DefaultListResource(List<T> data)
    {
        super(null);
        this.originData = data;
        this.result = _initList(data);
    }

    public DefaultListResource(List<T> data, String config)
    {
        super(config);
        this.result = _initList(data);
    }

    private List<LinkedHashMap<String, Object>> _initList(List<T> data)
    {
        if(data == null){
            data = new ArrayList<>();
        }
        List<LinkedHashMap<String, Object>> list = new ArrayList<>(data.size());
        for (T item: data
             ) {
            list.add(_initMap(item));
        }
        return list;
    }

    @Override
    public List<LinkedHashMap<String, Object>> result() {
        format();
        return result;
    }

    @Override
    public void modifyValue(String key, ModifyValue modifyValue) {
        for (Map<String, Object> item: result
        ) {
            _modifyValue(item, key, modifyValue);
        }
    }

    @Override
    public void modifyValue(String key, ModifyValueByMap modifyValueByMap) {
        for (Map<String, Object> item: result
        ) {
            _modifyValue(item, key, modifyValueByMap);
        }

    }

    @Override
    public void modifyItem(ModifyItem modifyItem)
    {
        for (Map<String, Object> item: result
        ) {
            modifyItem.modify(item);
        }
    }

    /**
     * 去除非配置属性
     */
    protected void format()
    {
        for (LinkedHashMap<String, Object> map: result
             ) {
            formatByListConfig(map);
        }

    }

}
