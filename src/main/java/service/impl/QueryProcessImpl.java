package service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import annotation.ListField;
import common.util.SearchUtil;
import service.ListConfigService;
import service.logic.Filter;
import service.logic.ListConfig;
import service.logic.relation.RelationSearch;
import vo.QueryProcessVo;
import vo.SearchVo;
import vo.search.FilterVo;
import service.QueryProcess;
import vo.search.GroupVo;
import vo.search.SortOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Service("queryProcess")
public class QueryProcessImpl<T> implements QueryProcess<T> {

    @Autowired
    private RelationSearch relation;

    @Autowired
    private Filter filter;

    @Autowired
    private ListConfigService listConfigService;

    @Override
    public QueryProcessVo<T> execute(SearchVo searchVo, String config) {
        QueryProcessVo<T> processVo = new QueryProcessVo<>();

        processVo.setQueryWrapper(getQueryWrapper(searchVo, config));
        processVo.setIpage(getPage(searchVo));

        return processVo;
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
    private QueryWrapper<T> getQueryWrapper(SearchVo searchVo, String config)
    {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        ListConfig listConfig = listConfigService.getListConfig(config);

        doKeys(queryWrapper, searchVo, listConfig);
        doGroups(queryWrapper, searchVo, listConfig);
        doSortOrders(queryWrapper, searchVo, listConfig);

        return queryWrapper;
    }

    /**
     * 处理关键字
     */
    private void doKeys(QueryWrapper<?> queryWrapper, SearchVo searchVo, ListConfig listConfig)
    {
        if(listConfig != null && searchVo.getKey() != null && ! searchVo.getKey().isEmpty()){
            String table = SearchUtil.getEntityTable(listConfig.getEntityClass());
            queryWrapper.and(q->{
                for (String field: listConfig.keys()
                ) {
                    q.or().like(table + "." + SearchUtil.fieldFormat(field, "_"), searchVo.getKey());
                }
            });

        }
    }

    /**
     * 处理group组合
     */
    private void doGroups(QueryWrapper<?> queryWrapper, SearchVo searchVo, ListConfig listConfig)
    {
        if(searchVo.getGroups() != null && ! searchVo.getGroups().isEmpty()){
            for (GroupVo group: searchVo.getGroups()
            ) {
                if(group.getFilters().isEmpty()){
                    continue;
                }
                queryWrapper.and(q -> this.doFilters(q, group.getFilters(), listConfig));
            }
        }
    }

    /**
     * 处理排序
     */
    private void doSortOrders(QueryWrapper<?> queryWrapper, SearchVo searchVo, ListConfig listConfig)
    {
        if(searchVo.getSortOrders() != null){
            String table = SearchUtil.getEntityTable(listConfig.getEntityClass());
            for (SortOrderVo orderVo: searchVo.getSortOrders()
            ) {
                Field field = SearchUtil.getDeclaredFieldSource(listConfig.getEntityClass(), orderVo.getField());
                if(field.isAnnotationPresent(ListField.class)){
                    ListField l = field.getAnnotation(ListField.class);
                    orderVo.setField(l.code());
                }else{
                    orderVo.setField(table + "." + SearchUtil.fieldFormat(orderVo.getField(), "_"));
                }
                filter.buildSortOrder(queryWrapper, orderVo);
            }
        }
    }

    /**
     * 处理过滤组
     */
    private void doFilters(QueryWrapper<?> queryWrapper, List<FilterVo> filterVos, ListConfig listConfig)
    {
        for (FilterVo filterVo: filterVos
        ) {
            queryWrapper.or();
            this.doFilter(queryWrapper, filterVo, listConfig);
        }
    }

    /**
     * 执行过滤
     */
    public void doFilter(QueryWrapper<?> queryWrapper, FilterVo filterVo, ListConfig listConfig)
    {
        if(filterVo.getField().contains(".")){
            String sql = relation.search(listConfig.getEntityClass(), filterVo);
            if(! sql.isEmpty()){
                queryWrapper.exists(sql);
            }
        }else{
            if(SearchUtil.isTableField(listConfig.getEntityClass(), filterVo.getField())){
                String table = SearchUtil.getEntityTable(listConfig.getEntityClass());
                Field field = SearchUtil.getDeclaredFieldSource(listConfig.getEntityClass(), filterVo.getField());
                if(field.isAnnotationPresent(ListField.class)){
                    ListField l = field.getAnnotation(ListField.class);
                    if(! "".equals(l.code())){
                        filterVo.setField(l.code());
                        doFilter(queryWrapper, filterVo, listConfig);
                        return ;
                    }
                }
                filterVo.setField(table + "." + filterVo.getField());
                filter.buildQuery(queryWrapper, filterVo);
            }
        }
    }




}
