package service;

import vo.QueryProcessVo;
import vo.SearchVo;

public interface QueryProcess<T> {
    QueryProcessVo<T> execute(SearchVo searchVo, String config);

}
