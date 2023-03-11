package com.zwh.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.utils.PageUtils;
import com.zwh.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 17:17:11
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

