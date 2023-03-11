package com.zwh.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.utils.PageUtils;
import com.zwh.gulimember.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:21:54
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

