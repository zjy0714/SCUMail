package com.zwh.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.utils.PageUtils;
import com.zwh.gulimember.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:21:53
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    MemberLevelEntity getDefaultLevel();
}

