package vo;

import lombok.Data;

import java.util.List;

@Data
public class SelectOptionVo {

    /**
     * 标题
     */
    private String label;

    /**
     * 值
     */
    private String value;

    /**
     * 是否可选
     */
    private Boolean disabled;

    /**
     * 子列表
     */
    private List<SelectOptionVo> optgroup;

}
