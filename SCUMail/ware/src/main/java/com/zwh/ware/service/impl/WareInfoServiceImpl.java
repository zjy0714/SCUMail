package com.zwh.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.zwh.common.utils.R;
import com.zwh.ware.feign.MemberFeignService;
import com.zwh.ware.vo.FareResponseVo;
import com.zwh.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwh.common.utils.PageUtils;
import com.zwh.common.utils.Query;

import com.zwh.ware.dao.WareInfoDao;
import com.zwh.ware.entity.WareInfoEntity;
import com.zwh.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            queryWrapper.eq("id",key).
                    or().like("name",key).
                    or().like("address",key).
                    or().like("areacode",key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据用户的收货地址计算运费
     * @param addrId
     * @return
     */
    @Override
    public FareResponseVo getFare(Long addrId) {
        FareResponseVo fareResponseVo = new FareResponseVo();
        R addrInfo = memberFeignService.addrInfo(addrId);
        MemberAddressVo data = addrInfo.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {
        });
        if (data != null){
            String phone = data.getPhone();
            String s = phone.substring(phone.length()-1);
            BigDecimal fare = new BigDecimal(s);
            fareResponseVo.setMemberAddressVo(data);
            fareResponseVo.setFare(fare);
            return fareResponseVo;
        }
        return null;
    }

}