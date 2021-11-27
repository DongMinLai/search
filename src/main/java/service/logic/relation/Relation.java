package service.logic.relation;

import service.RelationQueryProcess;
import vo.search.FilterVo;

import java.util.List;

public interface Relation {

    void loadAll(List<?> entityList, String relation);

    void loadAll(List<?> entityList, String relation, RelationQueryProcess relationQueryProcess);

    String searchSql(String relation);

    String searchSql(String relation, RelationQueryProcess relationQueryProcess);

}
