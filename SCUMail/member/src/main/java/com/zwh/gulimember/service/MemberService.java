package com.zwh.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwh.common.utils.PageUtils;
import com.zwh.gulimember.entity.MemberEntity;
import com.zwh.gulimember.exception.PhoneExistException;
import com.zwh.gulimember.exception.UsernameExitException;
import com.zwh.gulimember.vo.MemberLoginVo;
import com.zwh.gulimember.vo.RegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-27 16:21:54
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(RegisterVo vo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUserNameUnique(String name) throws UsernameExitException;

    MemberEntity login(MemberLoginVo vo);
}

