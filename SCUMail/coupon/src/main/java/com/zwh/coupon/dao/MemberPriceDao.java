package com.zwh.coupon.dao;

import com.zwh.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:06:07
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
