package vo.relation;

import lombok.Data;

import java.util.List;

@Data
public class BelongToManyVo extends RelationVo{

    /**
     * 中间表表名
     */
    private String middleTableName;

    /**
     * 查询列
     */
    private String columns;

    /**
     * 关联主键[主表]
     */
    private String primaryKey;

    /**
     * 关联主键[关联表]
     */
    private String relatedPrimaryKey;

    /**
     * 主表关系外键【中间表】
     */
    private String foreignPivotKey;

    /**
     * 关联表关系外键【中间表】
     */
    private String relatedPivotKey;
}