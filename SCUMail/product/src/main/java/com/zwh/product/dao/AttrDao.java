package com.zwh.guliproduct.dao;

import com.zwh.guliproduct.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-26 18:12:53
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSearchAttrIds(@Param("attr_ids") List<Long> attrIds);
}
