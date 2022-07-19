package com.relation.controller;

import com.relation.common.util.R;
import com.relation.exception.NoSuchListConfigException;
import com.relation.resource.Resource;
import com.relation.service.ListConfigService;
import com.relation.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/search/list")
public class ListConfigController {

    @Autowired
    private ListConfigService listConfigService;

    @RequestMapping("config")
    public R listConfig(@RequestParam String key)
    {
        try {
            ListConfigVo listConfig = listConfigService.getListConfigVo(key);
            return R.ok().putData(listConfig);
        }catch (NoSuchListConfigException e){
            return R.error(e.getMessage());
        }
    }
    @RequestMapping("config/custom")
    public R listConfig(@RequestBody CustomConfigVo customConfigVo)
    {
        try {
            ListConfigVo listConfig = listConfigService.getListConfigVo(customConfigVo);
            return R.ok().putData(listConfig);
        }catch (NoSuchListConfigException e){
            return R.error(e.getMessage());
        }
    }


    @RequestMapping("/{key}")
    public R list(@PathVariable("key") String key, @RequestBody SearchVo searchVo)
    {
        try {
            Resource<?, ?> resource = listConfigService.getResource(key,searchVo);
            return R.ok().putData(resource.result());
        }catch (NoSuchListConfigException e){
            return R.error(e.getMessage());
        }
    }
}
