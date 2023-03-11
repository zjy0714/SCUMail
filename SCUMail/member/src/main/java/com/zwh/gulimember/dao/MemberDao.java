package com.zwh.gulimember.dao;

import com.zwh.gulimember.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:21:54
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
