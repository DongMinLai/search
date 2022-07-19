package com.relation.vo;

import com.relation.vo.search.GroupVo;
import com.relation.vo.search.SortOrderVo;
import lombok.Data;

import java.util.List;

@Data
public class SearchVo{

    /**
     * 关键字
     */
    private String key;

    /**
     * 当前页
     */
    private Integer page = 1;

    /**
     * 默认数据大小
     */
    private Integer pageSize = 15;

    /**
     * 搜索内容
     */
    private List<GroupVo> groups;

    /**
     * 排序内容
     */
    private List<SortOrderVo> sortOrders;

    /**
     * 父级ID【用于二级页面参数】
     */
    private Long parentId;
}
