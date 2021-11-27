package vo.list;

import enums.ActionType;
import lombok.Data;

@Data
public class ActionVo {

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
     * 请求地址
     */
    private String url;

    /**
     * 请求方式
     */
    private String urlMethod;

    /**
     * 处理类型
     */
    private ActionType type;

}
