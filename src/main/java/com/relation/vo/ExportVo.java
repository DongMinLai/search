package com.relation.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class ExportVo implements Serializable {

    private List<LinkedHashMap<String, Object>> data;

    private LinkedHashMap<String, String> header;

}
