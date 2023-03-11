package com.zwh.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.utils.PageUtils;
import com.zwh.ware.entity.WareInfoEntity;
import com.zwh.ware.vo.FareResponseVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 17:17:11
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareResponseVo getFare(Long addrId);
}

