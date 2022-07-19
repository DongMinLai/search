package com.relation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.relation.enums.InputType;
import com.relation.exception.NoSuchListConfigException;
import com.relation.exception.SourceException;
import com.relation.resource.DefaultPageResource;
import com.relation.resource.Resource;
import com.relation.service.logic.ListConfig;
import com.relation.service.ListConfigService;
import com.relation.vo.*;
import com.relation.vo.list.ActionVo;
import com.relation.vo.list.FieldVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("listConfigService")
public class ListConfigServiceImpl implements ListConfigService {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public ListConfig getListConfig(String key) {
        try {
            return (ListConfig) applicationContext.getBean(key);
        }catch (Exception e){
            throw new NoSuchListConfigException("列表不存在");
        }
    }

    @Override
    public ListConfigVo getListConfigVo(String key)
    {
        ListConfig listConfig = this.getListConfig(key);
        return listConfig.getListConfig();
    }

    @Override
    public ListConfigVo getListConfigVo(CustomConfigVo customConfigVo)
    {
        ListConfig listConfig = this.getListConfig(customConfigVo.getConfig());
        ListConfigVo listConfigVo = listConfig.getListConfig();
        ListConfigVo listConfigVoClone = new ListConfigVo();
        BeanUtil.copyProperties(listConfigVo,listConfigVoClone);
        Map<String, FieldVo> fields = listConfigVoClone.getFields().stream().collect(Collectors.toMap(FieldVo::getCode, Function.identity()));
        List<FieldVo> fieldVos = new ArrayList<>();
        customConfigVo.getFields().forEach(e->{
            if (fields.containsKey(e)){
                fieldVos.add(fields.get(e));
            }
        });
        listConfigVoClone.setFields(fieldVos);
        Map<String, ActionVo> actions = listConfigVoClone.getActions().stream().collect(Collectors.toMap(ActionVo::getCode, Function.identity()));
        List<ActionVo> actionVos = new ArrayList<>();
        customConfigVo.getActions().forEach(e->{
            if (actions.containsKey(e)){
                actionVos.add(actions.get(e));
            }
        });
        listConfigVoClone.setActions(actionVos);
        return listConfigVoClone;
    }



    @Override
    public Resource<?, ?> getResource(String key, SearchVo searchVo) {
        ListConfig listConfig = this.getListConfig(key);
        return listConfig.data(searchVo);
    }

    @Override
    public ExportVo getExportData(String key, SearchVo searchVo) {
        searchVo.setPageSize(-1);
        DefaultPageResource<?> page = (DefaultPageResource<?>) getResource(key, searchVo);
        if(page == null){
            throw new SourceException("导出异常：数据源配置异常！");
        }
        ListConfigVo vo = getListConfigVo(key);
        LinkedHashMap<String, String> header = new LinkedHashMap<>();
        String children = vo.getUnfold();
        Map<String, Map<String, String> > selectLabel = new HashMap<>();  //如果一个属性为select类型并设置了options, 就不必对结果再做转换， 不然无法获取属性label值
        vo.getFields().stream().filter(FieldVo::getIsVisible).forEach((field)->{
            if(field.getInputType().equals(InputType.SELECT) && ! ObjectUtils.isEmpty(field.getOptions())){
                if(! selectLabel.containsKey(field.getCode())){
                    selectLabel.put(field.getCode(), new HashMap<>());
                }
                for (SelectOptionVo selectOptionVo: field.getOptions()
                ) {
                    selectLabel.get(field.getCode()).put(selectOptionVo.getValue().toString(), selectOptionVo.getLabel());
                }
            }
            header.put(field.getCode(), field.getLabel());
        });

        List<LinkedHashMap<String, Object>> exportList = new ArrayList<>();
        page.result().getList().forEach(map->{
            List<Object> childrenBean = null;
            if(! "".equals(children)){
                childrenBean = (List<Object>)map.get(children);
            }
            map.keySet().removeIf(k->! header.containsKey(k));
            if(! ObjectUtils.isEmpty(selectLabel)){
                for (String column:map.keySet()
                ) {
                    if(selectLabel.containsKey(column)){
                        map.put(column, selectLabel.get(column).getOrDefault(map.get(column) == null ? "" :map.get(column).toString(), ""));
                    }
                }
            }

            exportList.add(map);
            if(! "".equals(children) && ! CollectionUtils.isEmpty(childrenBean)){
                childrenBean.remove(0);
                for (Object bean: childrenBean
                ) {
                    LinkedHashMap<String, Object> child = new LinkedHashMap<>(BeanUtil.beanToMap(bean));
                    LinkedHashMap<String, Object> tmp = new LinkedHashMap<>();
                    header.keySet().forEach(hk->{
                        tmp.put(hk,child.getOrDefault(hk, map.getOrDefault(hk, null)));
                    });
                    exportList.add(tmp);
                }
            }
        });
        ExportVo exportVo = new ExportVo();
        exportVo.setData(exportList);
        exportVo.setHeader(header);
        return exportVo;
    }


}
