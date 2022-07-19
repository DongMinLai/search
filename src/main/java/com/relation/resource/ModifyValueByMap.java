package com.relation.resource;

import java.util.Map;

public interface ModifyValueByMap {

    Object modify(String key, Object value, Map<String, Object> map);

}
