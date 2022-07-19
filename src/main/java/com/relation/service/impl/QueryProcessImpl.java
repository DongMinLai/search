package com.relation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.relation.annotation.ListField;
import com.relation.common.util.FilterUtil;
import com.relation.common.util.SearchUtil;
import com.relation.enums.ConditionType;
import com.relation.service.ListConfigService;
import com.relation.service.logic.Filter;
import com.relation.service.logic.ListConfig;
import com.relation.service.logic.relation.RelationSearch;
import com.relation.vo.QueryProcessVo;
import com.relation.vo.SearchVo;
import com.relation.vo.search.FilterVo;
import com.relation.service.QueryProcess;
import com.relation.vo.search.GroupVo;
import com.relation.vo.search.SortOrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@Service("queryProcess")
public class QueryProcessImpl<T> implements QueryProcess<T> {

    /**
     * 关联查询【关联数据满足】
     */
    public static final String RELATION_NODE = ".";

    /**
     * 合并查询【所有字段满足】
     */
    public static final String AND_NODE = "^";

    /**
     * 联合查询【多个字段有一个满足】
     */
    public static final String OR_NODE = "|";

    @Autowired
    private RelationSearch relation;

    @Autowired
    private Filter filter;

    @Autowired
    private ListConfigService listConfigService;

    @Override
    public QueryProcessVo<T> execute(SearchVo searchVo, String config) {
        ListConfig listConfig = listConfigService.getListConfig(config);
        return execute(searchVo, listConfig.getEntityClass(), listConfig.keys());
    }

    @Override
    public QueryProcessVo<T> execute(SearchVo searchVo, Class<?> entityClass, Collection<String> keys) {
        QueryProcessVo<T> processVo = new QueryProcessVo<>();
        processVo.setQueryWrapper(getQueryWrapper(searchVo, entityClass, keys));
        processVo.setIpage(getPage(searchVo));
        return processVo;
    }

    @Override
    public QueryProcessVo<T> execute(SearchVo searchVo, Class<?> entityClass) {
        return execute(searchVo, entityClass, null);
    }

    /**
     * 获取分页对象
     */
    private IPage<T> getPage(SearchVo searchVo)
    {
        return new Page<T>(searchVo.getPage(), searchVo.getPageSize());
    }

    /**
     * 获取查询语句对象
     */
    private QueryWrapper<T> getQueryWrapper(SearchVo searchVo, Class<?> entityClass, Collection<String> keys)
    {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        doKeys(queryWrapper, searchVo, entityClass, keys);
        doGroups(queryWrapper, searchVo, entityClass);
        doSortOrders(queryWrapper, searchVo, entityClass);

        return queryWrapper;
    }

    /**
     * 处理关键字
     */
    private void doKeys(QueryWrapper<?> queryWrapper, SearchVo searchVo, Class<?> entityClass, Collection<String> keys)
    {
        if(entityClass != null && ! ObjectUtils.isEmpty(searchVo.getKey())){
            searchVo.setKey(searchVo.getKey().trim());
            queryWrapper.and(q->{
                for (String field: keys
                ) {
                    buildKeyQuery(field, entityClass, q, searchVo);
                }
            });

        }
    }

    private void buildKeyQuery(String field, Class<?> entityClass, QueryWrapper<?> q, SearchVo searchVo)
    {
        if(field.contains(RELATION_NODE)){  //如果关键字有`.`隔开
            String[] searchFields = field.split("\\.",2);
            if(SearchUtil.isTableField(entityClass, searchFields[0])){  //关联关系查询
                doFilter(q.or(), new FilterVo(){{
                    setField(field);
                    setValue(searchVo.getKey());
                }}, entityClass);
            }else{  //关联表查询
                q.or().like(SearchUtil.fieldFormat(field, "_"), searchVo.getKey());
            }
        }else{  //主表字段查询
            String prefix = getPrefixByEntity(entityClass);
            q.or().like(prefix + SearchUtil.fieldFormat(field, "_"), searchVo.getKey());
        }

    }

    /**
     * 处理group组合
     */
    private void doGroups(QueryWrapper<?> queryWrapper, SearchVo searchVo, Class<?> entityClass)
    {
        if(searchVo.getGroups() != null && ! searchVo.getGroups().isEmpty()){
            for (GroupVo group: searchVo.getGroups()
            ) {
                if(group.getFilters().isEmpty()){
                    continue;
                }
                queryWrapper.and(q -> this.doFilters(q, group.getFilters(), entityClass));
            }
        }
    }

    /**
     * 处理排序
     */
    private void doSortOrders(QueryWrapper<?> queryWrapper, SearchVo searchVo, Class<?> entityClass)
    {
        if(searchVo.getSortOrders() != null){
            String prefix = getPrefixByEntity(entityClass);
            for (SortOrderVo orderVo: searchVo.getSortOrders()
            ) {
                Field field = SearchUtil.getDeclaredFieldSource(entityClass, orderVo.getField());
                if(field.isAnnotationPresent(ListField.class)){
                    ListField l = field.getAnnotation(ListField.class);
                    if (! "".equals(l.queryCode())) {
                        orderVo.setField(l.queryCode());
                    }else if (! "".equals(l.code())) {
                        orderVo.setField(l.code());
                    }else{
                        orderVo.setField(prefix + SearchUtil.fieldFormat(orderVo.getField(), "_"));
                    }
                }else{
                    orderVo.setField(prefix + SearchUtil.fieldFormat(orderVo.getField(), "_"));
                }
                filter.buildSortOrder(queryWrapper, orderVo);
            }
        }

        if(searchVo.getGroups() != null){
            for (GroupVo group: searchVo.getGroups()
            ) {
                if(group.getFilters().isEmpty()){
                    continue;
                }
                group.getFilters().stream().filter(item-> !item.getField().contains(".") && item.getConditionType().equals(ConditionType.IN))
                        .forEach(item->{
                            Collection<?> inValue = FilterUtil.getValueAsList(item.getValue());
                            if(! inValue.isEmpty()){
                                queryWrapper.orderByAsc("FIELD("+SearchUtil.fieldFormat(item.getField(), "_")+", '"+ Joiner.on("','").join(inValue) +"')");
                            }
                        });
            }
        }
    }

    /**
     * 处理过滤组
     */
    private void doFilters(QueryWrapper<?> queryWrapper, List<FilterVo> filterVos, Class<?> entityClass)
    {
        for (FilterVo filterVo: filterVos
        ) {
            queryWrapper.or();
            if (filterVo.getValue() instanceof String){
                filterVo.setValue(filterVo.getValue().toString().trim());
            }
            this.doFilter(queryWrapper, filterVo, entityClass);
        }
    }

    /**
     * 执行过滤
     */
    public void doFilter(QueryWrapper<?> queryWrapper, FilterVo filterVo, Class<?> entityClass)
    {
        if(filterVo.getField().contains(RELATION_NODE)){  //关联查询
            String sql = relation.search(entityClass, filterVo);
            if(! sql.isEmpty()){
                queryWrapper.exists(sql);
            }
        }else if(filterVo.getField().contains(AND_NODE)){  //合并与查询
            queryWrapper.and(q->{
                for (String fieldStr:filterVo.getField().split("\\" + AND_NODE)
                ) {
                    FilterVo eVo = new FilterVo();
                    BeanUtils.copyProperties(filterVo, eVo);
                    eVo.setField(fieldStr);
                    doFilter(q, eVo, entityClass);
                }
            });
        }else if(filterVo.getField().contains(OR_NODE)){  //合并或查询
            queryWrapper.or(q->{
                for (String fieldStr:filterVo.getField().split("\\" + OR_NODE)
                ) {
                    FilterVo eVo = new FilterVo();
                    BeanUtils.copyProperties(filterVo, eVo);
                    eVo.setField(fieldStr);
                    doFilter(q, eVo, entityClass);
                }
            });
        }else if(SearchUtil.isTableField(entityClass, filterVo.getField())){
            Field field = SearchUtil.getDeclaredFieldSource(entityClass, filterVo.getField());
            if (field.isAnnotationPresent(ListField.class)) {
                ListField l = field.getAnnotation(ListField.class);
                if (! "".equals(l.queryCode())) {
                    filterVo.setField(l.queryCode());
                }else if (! "".equals(l.code())) {
                    filterVo.setField(l.code());
                }
            }
            buildQuery(queryWrapper, filterVo, entityClass);
        }
    }

    private void buildQuery(QueryWrapper<?> queryWrapper, FilterVo filterVo, Class<?> entityClass)
    {
        String prefix = getPrefixByEntity(entityClass);
        filterVo.setField(prefix + filterVo.getField());
        filter.buildQuery(queryWrapper, filterVo);
    }


    private String getPrefixByEntity(Class<?> entityClass)
    {
        String table = SearchUtil.getEntityTable(entityClass);
        return "".equals(table) ? "" : table + ".";
    }

}
