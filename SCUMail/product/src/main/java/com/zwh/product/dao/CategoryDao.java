package com.zwh.guliproduct.dao;

import com.zwh.guliproduct.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-26 18:12:53
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
