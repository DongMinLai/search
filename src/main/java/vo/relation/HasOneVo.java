package vo.relation;

import lombok.Data;

@Data
public class HasOneVo extends RelationVo{

    /**
     * 查询列
     */
    private String columns;

    /**
     * 关联主键[主表]
     */
    private String primaryKey;

    /**
     * 关联外键[关联表]
     */
    private String foreignKey;

}
