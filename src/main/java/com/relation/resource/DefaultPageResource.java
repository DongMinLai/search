package com.relation.resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.relation.vo.ListVo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultPageResource<T> extends AbstractResult<T, ListVo<LinkedHashMap<String, Object>>>{

    protected ListVo<LinkedHashMap<String, Object>> result;


    protected IPage<T> originData;

    public DefaultPageResource(IPage<T> data)
    {
        super(null);
        this.originData = data;
        this.result = _initPage(data);
    }

    public DefaultPageResource(IPage<T> data, String config)
    {
        super(config);
        this.result = _initPage(data);
    }

    private ListVo<LinkedHashMap<String, Object>> _initPage(IPage<T> data)
    {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>(data.getRecords().size());
        for (T item:data.getRecords()
             ) {
            list.add(_initMap(item));
        }
        result = new ListVo<>();
        result.setTotalPage((int) data.getPages());
        result.setPageSize((int)data.getSize());
        result.setCurrPage((int)data.getCurrent());
        result.setTotalCount((int)data.getTotal());
        result.setList(list);
        return result;
    }

    @Override
    public ListVo<LinkedHashMap<String, Object>> result() {
        format();
        return result;
    }

    @Override
    public void modifyValue(String key, ModifyValue modifyValue) {
        for (Map<String, Object> item: result.getList()
        ) {
            _modifyValue(item, key, modifyValue);
        }
    }

    @Override
    public void modifyValue(String key, ModifyValueByMap modifyValueByMap) {
        for (Map<String, Object> item: result.getList()
        ) {
            _modifyValue(item, key, modifyValueByMap);
        }
    }

    @Override
    public void modifyItem(ModifyItem modifyItem) {
        for (Map<String, Object> item: result.getList()
        ) {
            modifyItem.modify(item);
        }
    }

    public IPage<T> getOriginData() {
        return originData;
    }

    /**
     * 去除非配置属性
     */
    protected void format()
    {
        for (LinkedHashMap<String, Object> map: result.getList()
             ) {
            formatByListConfig(map);
        }
    }
}
