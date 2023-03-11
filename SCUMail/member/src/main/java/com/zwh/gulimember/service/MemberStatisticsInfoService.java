package com.zwh.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.utils.PageUtils;
import com.zwh.gulimember.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:21:53
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

