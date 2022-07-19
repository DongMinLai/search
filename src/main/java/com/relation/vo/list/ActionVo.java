package com.relation.vo.list;

import com.relation.enums.ActionType;
import com.relation.enums.ParameterFrom;
import com.relation.enums.UrlMethod;
import lombok.Data;

import java.io.Serializable;

@Data
public class ActionVo implements Serializable {

    /**
     * 标题
     */
    private String label;

    /**
     * 操作标识
     */
    private String code;

    /**
     * 数据列
     */
    private String column;

    /**
     * 参数源
     */
    private ParameterFrom parameterFrom;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求方式
     */
    private UrlMethod urlMethod;

    /**
     * 处理类型
     */
    private ActionType type;

    /**
     * 是否显示
     */
    private Boolean isVisible;

    public ActionVo() {
        this.label = "";
        this.code = "";
        this.column = "";
        this.parameterFrom = ParameterFrom.RESULT;
        this.url = "";
        this.urlMethod = UrlMethod.POST;
        this.type = ActionType.API;
        this.isVisible = true;
    }
}
