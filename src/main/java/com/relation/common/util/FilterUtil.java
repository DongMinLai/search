package com.relation.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

public class FilterUtil {

    /**
     * 解析日期并累加一天
     * @param str
     * @return
     */
    public static String formatDateValue(String str) {
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


    public static Collection<?> getValueAsList(Object value)
    {
        if(value instanceof String && ((String) value).contains(",")){
            String[] strs = ((String) value).split(",");
            return Arrays.asList(strs);
        }
        if(value instanceof Collection){
            return (Collection<?>)value;
        }
        return Collections.singletonList(value);
    }

}
