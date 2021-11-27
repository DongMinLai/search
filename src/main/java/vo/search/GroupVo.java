package vo.search;

import lombok.Data;

import java.util.List;

@Data
public class GroupVo {

    /**
     * 条件列表
     */
    private List<FilterVo> filters;

}
