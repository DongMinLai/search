package vo;

import lombok.Data;
import vo.list.ActionVo;
import vo.list.FieldVo;

import java.util.List;

/**
 * 列表配置
 */
@Data
public class ListConfigVo {

    /**
     * 数据接口地址
     */
    private String api;

    /**
     * 关键字搜索
     */
    private List<String> kye;

    /**
     * 列表字段
     */
    private List<FieldVo> fields;

    /**
     * 展开数据
     */
    private String unfold;

    /**
     * 操作按钮
     */
    private List<ActionVo> actions;

    /**
     * 批处理
     */
    private List<ActionVo> batchActions;

    /**
     * 用户自定义显示
     */
    private List<String> userView;

}
