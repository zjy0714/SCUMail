package com.zwh.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.utils.PageUtils;
import com.zwh.gulimember.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:21:54
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

