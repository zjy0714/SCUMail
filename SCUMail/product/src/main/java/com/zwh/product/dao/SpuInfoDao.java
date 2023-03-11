package com.zwh.guliproduct.dao;

import com.zwh.guliproduct.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-26 18:12:52
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spu_id") Long spuId, @Param("code") int code);
}
