package resource;

import cn.hutool.extra.spring.SpringUtil;
import common.util.SearchUtil;
import service.logic.ListConfig;
import vo.list.FieldVo;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractResult<T, R> implements Resource<T, R>{

    private static final Map<String, List<Field>> fields = new HashMap<>();  //字段缓存
    private static final Map<String, List<Method>> getMethods = new HashMap<>(); //方法缓存

    /**
     * 列表配置
     */
    protected ListConfig listConfig;

    public AbstractResult(){}

    public AbstractResult(String config)
    {
        if(config != null && !"".equals(config)){
            ApplicationContext applicationContext = SpringUtil.getApplicationContext();
            this.listConfig = (ListConfig) applicationContext.getBean(config);
        }
    }

    @Override
    public abstract R result();

    @Override
    public abstract void modifyValue(String key, ModifyValue modifyValue);

    @Override
    public abstract void modifyValue(String key, ModifyValueByMap modifyValueByMap);

    /**
     * 修改Map元素
     * @param map
     * @param key
     * @param modifyValueByMap
     */
    protected void _modifyValue(Map<String, Object>map, String key, ModifyValueByMap modifyValueByMap)
    {
        Object newValue = modifyValueByMap.modify(key, map.getOrDefault(key, null), map);
        map.put(key, newValue);
    }

    /**
     * 修改Map元素
     * @param map
     * @param key
     * @param modifyValue
     */
    protected void _modifyValue(Map<String, Object>map, String key, ModifyValue modifyValue)
    {
        Object newValue = modifyValue.modify(key, map.getOrDefault(key, null));
        map.put(key, newValue);
    }


    protected Map<String, Object> _initMap(T data){

        try {
            List<Method> methods = getMethods(data);
            Map<String, Object> map = new HashMap<>(methods.size());
            for (Method method: methods
                 ) {
                String name = SearchUtil.getFieldNameByMethod(method.getName());
                map.put(name, method.invoke(data));
            }
            return map;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return new HashMap<String, Object>();
        }
    }

    protected List<Field> getFields(T data)
    {
        String className = getDataClass(data).getName();
        if (! fields.containsKey(className)){
            fields.put(className, SearchUtil.getEntityFields(getDataClass(data)));
        }
        return fields.get(className);
    }

    protected Class<?> getDataClass(T data)
    {
        return data.getClass();
    }

    private List<Method> getMethods(T data) throws NoSuchMethodException {
        String className = getDataClass(data).getName();
        if (! getMethods.containsKey(className)){
            getMethods.put(className, SearchUtil.getEntityGetMethods(getDataClass(data)));
        }
        return getMethods.get(className);
    }

    /**
     * 去除非配置属性
     */
    protected void formatByListConfig(Map<String, Object>map)
    {
        if(listConfig != null){
            Set<String> shows = listConfig.getListConfig().getFields().stream().map(FieldVo::getCode).collect(Collectors.toSet());
            Set<String> hidden = new HashSet<>();
            for (String code: map.keySet()
            ) {
                if(! shows.contains(code)){
                    hidden.add(code);
                }
            }
            for (String code:hidden
            ) {
                map.remove(code);
            }
        }
    }
}
