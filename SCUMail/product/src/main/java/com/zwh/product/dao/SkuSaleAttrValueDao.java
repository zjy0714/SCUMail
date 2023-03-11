package com.zwh.guliproduct.dao;

import com.zwh.guliproduct.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zwh.guliproduct.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-26 18:12:52
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrsBySpuId(@Param("spu_id") Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(@Param("sku_id") Long skuId);
}
