package vo.relation;

import lombok.Data;

import java.util.List;

@Data
public class RelationVo {

    /**
     * 关联表名
     */
    private String tableName;

    /**
     * 关联值
     */
    private List<Object> keyValue;
}
