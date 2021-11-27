package service;

import service.logic.ListConfig;
import vo.ListConfigVo;

public interface ListConfigService {

    /**
     * 获取列表类型
     */
    ListConfig getListConfig(String key);

    /**
     * 获取列表类型
     */
    ListConfigVo getListConfigVo(String key);
}
