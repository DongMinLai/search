package vo;

import lombok.Data;
import vo.search.GroupVo;
import vo.search.SortOrderVo;

import java.util.List;

@Data
public class SearchVo{

    /**
     * 关键字
     */
    private String key;

    private Integer page = 1;

    private Integer pageSize = 15;

    private List<GroupVo> groups;

    private List<SortOrderVo> sortOrders;

}
