package resource;

import common.util.SearchUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultListResource<T> extends AbstractResult<T, List<Map<String, Object>>>{

    protected List<Map<String, Object>> result;

    public DefaultListResource(List<T> data)
    {
        super(null);
        this.result = _initList(data);
    }

    public DefaultListResource(List<T> data, String config)
    {
        super(config);
        this.result = _initList(data);
    }

    private List<Map<String, Object>> _initList(List<T> data)
    {
        if(data == null){
            data = new ArrayList<>();
        }
        List<Map<String, Object>> list = new ArrayList<>(data.size());
        for (T item: data
             ) {
            list.add(_initMap(item));
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> result() {
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

    /**
     * 去除非配置属性
     */
    protected void format()
    {
        for (Map<String, Object> map: result
             ) {
            formatByListConfig(map);
        }

    }

}
