package vo.list;

import enums.ConditionType;
import enums.InputType;
import enums.ValueType;
import vo.SelectOptionVo;
import lombok.Data;

import java.util.List;

@Data
public class FieldVo {

    public FieldVo()
    {
        this.isVisible = true;
        this.isFilterable = true;
        this.isSortable = true;
        this.conditionType = ConditionType.LIKE;
        this.valueType = ValueType.TEXT;
        this.inputType = InputType.TEXT;
    }

    /**
     * 标题
     */
    private String label;

    /**
     * 字段编码
     */
    private String code;

    /**
     * 是否显示
     */
    private Boolean isVisible;

    /**
     * 可过滤
     */
    private Boolean isFilterable;

    /**
     * 可排序
     */
    private Boolean isSortable;

    /**
     * 条件类型
     */
    private ConditionType conditionType;

    /**
     * 内容类型
     */
    private ValueType valueType;
    /**
     * 输入类型
     */
    private InputType inputType;

    /**
     * 可选数据列表
     */
    private List<SelectOptionVo> options;

    /**
     * 子列表
     */
    private List<FieldVo> children;

}
