package com.zwh.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.to.SkuReductionTo;
import com.zwh.common.utils.PageUtils;
import com.zwh.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:06:07
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

