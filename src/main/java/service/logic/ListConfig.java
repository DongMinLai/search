package service.logic;

import vo.ListConfigVo;
import vo.list.ActionVo;
import vo.list.FieldVo;

import java.util.List;

public interface ListConfig {

    /**
     * 配置对象
     */
    ListConfigVo getListConfig();

    /**
     * 数据接口
     */
    String api();

    /**
     * 关键字
     */
    List<String> keys();

    /**
     * 操作按钮
     */
    List<ActionVo> actions();

    /**
     * 批处理按钮
     */
    List<ActionVo> batchActions();

    /**
     * 可用字段列表
     */
    List<FieldVo> fields();

    /**
     * 主表实体
     */
    Class<?> getEntityClass();

    /**
     * 可过滤字段
     */
    List<String> filterableFields();
}
