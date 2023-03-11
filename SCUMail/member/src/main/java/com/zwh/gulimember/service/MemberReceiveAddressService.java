package com.zwh.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.utils.PageUtils;
import com.zwh.gulimember.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:21:53
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

