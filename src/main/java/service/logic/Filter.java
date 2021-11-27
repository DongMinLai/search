package service.logic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import common.util.SearchUtil;
import vo.search.FilterVo;
import vo.search.SortOrderVo;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class Filter {

    /**
     * 处理搜索条件
     */
    public void buildQuery(QueryWrapper<?> queryWrapper, FilterVo filterVo)
    {
        if(filterVo.getValue() == null || "".equals(filterVo.getValue()))
        {
            return ;
        }
        filterVo.setField(SearchUtil.fieldFormat(filterVo.getField(), "_"));
        switch (filterVo.getConditionType()){
            case NE:
                queryWrapper.ne(filterVo.getField(), filterVo.getValue());
                break;
            case GT:
                queryWrapper.gt(filterVo.getField(), filterVo.getValue());
                break;
            case GE:
                queryWrapper.ge(filterVo.getField(), filterVo.getValue());
                break;
            case LT:
                queryWrapper.lt(filterVo.getField(), filterVo.getValue());
                break;
            case LE:
                queryWrapper.le(filterVo.getField(), filterVo.getValue());
                break;
            case BETWEEN:
                if(filterVo.getValue() instanceof List<?>){
                    List<?> list =  (List<?>) filterVo.getValue();
                    if(list.size() == 2){
                        queryWrapper.between(filterVo.getField(), list.get(0), formatDateValue((String) list.get(1)));
                    }
                }else if(filterVo.getValue() instanceof String){
                    String String  = filterVo.getValue().toString();
                    if (String.contains(",")) {
                        List<String> values = this.stringToArray(String);
                        Object end = formatDateValue(values.get(1));
                        queryWrapper.between(filterVo.getField(), values.get(0), end);
                    } else {
                        queryWrapper.ge(filterVo.getField(), filterVo.getValue());
                    }
                }
                break;
            case NOT_BETWEEN:
                if(filterVo.getValue() instanceof List<?>){
                    List<?> list =  (List<?>) filterVo.getValue();
                    if(list.size() == 2) {
                        queryWrapper.notBetween(filterVo.getField(), list.get(0), formatDateValue((String) list.get(1)));
                    }
                }else if(filterVo.getValue() instanceof String){
                    String String  = filterVo.getValue().toString();
                    if(String.contains(",")){
                        List<String> values = this.stringToArray(String);
                        String end = formatDateValue(values.get(1));
                        queryWrapper.notBetween(filterVo.getField(), values.get(0), end);
                    }
                    else{
                        queryWrapper.lt(filterVo.getField(), filterVo.getValue());
                    }
                }

                break;
            case LIKE:
                queryWrapper.like(filterVo.getField(), filterVo.getValue());
                break;
            case LIKE_LEFT:
                queryWrapper.likeLeft(filterVo.getField(), filterVo.getValue());
                break;
            case LIKE_RIGHT:
                queryWrapper.likeRight(filterVo.getField(), filterVo.getValue());
                break;
            case IN:
                if(filterVo.getValue() instanceof String){
                    filterVo.setValue(stringToArray(filterVo.getValue().toString()));
                }
                queryWrapper.in(filterVo.getField(), filterVo.getValue());
                break;
            case NOT_IN:
                if(filterVo.getValue() instanceof String){
                    filterVo.setValue(stringToArray(filterVo.getValue().toString()));
                }
                queryWrapper.notIn(filterVo.getField(), filterVo.getValue());
                break;
            case IS_NULL:
                queryWrapper.isNull(filterVo.getField());
                break;
            case IS_NOT_NULL:
                queryWrapper.isNotNull(filterVo.getField());
                break;
            default:  //默认用相等处理
                queryWrapper.eq(filterVo.getField(), filterVo.getValue());
        }
    }

    /**
     * 处理排序
     */
    public void buildSortOrder(QueryWrapper<?> queryWrapper, SortOrderVo orderVo)
    {
        queryWrapper.orderBy(true, "ASC".equals(orderVo.getDirection().toUpperCase(Locale.ROOT)),
                orderVo.getField());
    }

    /**
     * 拆分字符串
     */
    public List<String> stringToArray(String str)
    {
        return Arrays.asList(str.split(","));
    }

    /**
     * 解析日期并累加一天
     * @param str
     * @return
     */
    private String formatDateValue(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            format.setLenient(false);
            Calendar cd = Calendar.getInstance();
            cd.setTime(format.parse(str));
            cd.add(Calendar.DATE, 1);
            return format.format(cd.getTime());
        } catch (ParseException e) {
            return str;
        }
    }
}
