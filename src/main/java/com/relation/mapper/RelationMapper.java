package com.relation.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.relation.model.handler.RelationResultHandler;
import com.relation.vo.relation.BelongToManyVo;
import com.relation.vo.relation.BelongToVo;
import com.relation.vo.relation.HasManyVo;
import com.relation.vo.relation.HasOneVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RelationMapper {

    void hasOne(@Param("hasOne")HasOneVo hasOneVo, RelationResultHandler relationResultHandler, @Param("ew")Wrapper<?> queryWrapper);

    void hasMany(@Param("hasMany") HasManyVo hasManyVo, RelationResultHandler relationResultHandler, @Param("ew")Wrapper<?> queryWrapper);

    void belongTo(@Param("belongTo") BelongToVo belongTo, RelationResultHandler relationResultHandler, @Param("ew")Wrapper<?> queryWrapper);

    void belongToMany(@Param("belongToMany") BelongToManyVo belongToMany, RelationResultHandler relationResultHandler, @Param("ew")Wrapper<?> queryWrapper);

}
