package resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import vo.ListVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultPageResource<T> extends AbstractResult<T, ListVo<Map<String, Object>>>{

    protected ListVo<Map<String, Object>> result;

    public DefaultPageResource(IPage<T> data)
    {
        super(null);
        this.result = _initPage(data);
    }

    public DefaultPageResource(IPage<T> data, String config)
    {
        super(config);
        this.result = _initPage(data);
    }

    private ListVo<Map<String, Object>> _initPage(IPage<T> data)
    {
        List<Map<String, Object>> list = new ArrayList<>(data.getRecords().size());
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
    public ListVo<Map<String, Object>> result() {
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

    /**
     * 去除非配置属性
     */
    protected void format()
    {
        for (Map<String, Object> map: result.getList()
             ) {
            formatByListConfig(map);
        }
    }
}
