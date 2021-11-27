package service.impl;

import exception.NoSuchListConfig;
import service.logic.ListConfig;
import service.ListConfigService;
import vo.ListConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("listConfigService")
public class ListConfigServiceImpl implements ListConfigService {

    @Autowired
    private Map<String, ListConfig> listConfigList;

    @Override
    public ListConfig getListConfig(String key) {
        return listConfigList.get(key);
    }

    @Override
    public ListConfigVo getListConfigVo(String key)
    {
        ListConfig listConfig = this.getListConfig(key);
        if(listConfig == null){
            throw new NoSuchListConfig("列表不存在");
        }
        return listConfig.getListConfig();
    }



}
