package resource;

import java.util.Map;

public class DefaultMapResource<T> extends AbstractResult<T, Map<String, Object>>{

    protected Map<String, Object> result;

    public DefaultMapResource(T data){
        this.result = _initMap(data);
    }

    @Override
    public Map<String, Object> result() {
        return result;
    }

    @Override
    public void modifyValue(String key, ModifyValue modifyValue) {
        _modifyValue(result, key, modifyValue);
    }

    @Override
    public void modifyValue(String key, ModifyValueByMap modifyValueByMap)
    {
        _modifyValue(result, key, modifyValueByMap);
    }

}
