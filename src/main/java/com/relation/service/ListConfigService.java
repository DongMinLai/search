package com.relation.service;

import com.relation.resource.Resource;
import com.relation.service.logic.ListConfig;
import com.relation.vo.CustomConfigVo;
import com.relation.vo.ExportVo;
import com.relation.vo.ListConfigVo;
import com.relation.vo.SearchVo;

public interface ListConfigService {

    /**
     * 获取列表类型
     */
    ListConfig getListConfig(String key);

    /**
     * 获取列表类型
     */
    ListConfigVo getListConfigVo(String key);

    /**
     * 获取列表类型
     */
    ListConfigVo getListConfigVo(CustomConfigVo customConfigVo);

    /**
     * 获取列表数据
     * @param searchVo
     * @return
     */
    Resource<?, ?> getResource(String key, SearchVo searchVo);

    /**
     * 获取导出数据
     * @param key
     * @param searchVo
     * @return
     */
    ExportVo getExportData(String key, SearchVo searchVo);
}
