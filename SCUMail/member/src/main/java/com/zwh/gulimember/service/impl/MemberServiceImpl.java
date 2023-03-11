package com.zwh.gulimember.service.impl;

import com.zwh.gulimember.entity.MemberLevelEntity;
import com.zwh.gulimember.exception.PhoneExistException;
import com.zwh.gulimember.exception.UsernameExitException;
import com.zwh.gulimember.service.MemberLevelService;
import com.zwh.gulimember.vo.MemberLoginVo;
import com.zwh.gulimember.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwh.common.utils.PageUtils;
import com.zwh.common.utils.Query;

import com.zwh.gulimember.dao.MemberDao;
import com.zwh.gulimember.entity.MemberEntity;
import com.zwh.gulimember.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService levelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(RegisterVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        //设置默认等级
        MemberLevelEntity levelEntity = levelService.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        //检查用户名和手机号是否唯一
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUsername());
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUsername());

        //密码加密处理
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        //保存
        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        Long count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0){
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String name) throws UsernameExitException{
        Long count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", name));
        if(count > 0){
            throw new UsernameExitException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginAcct = vo.getLoginAcct();
        String password = vo.getPassword();

        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginAcct).or().eq("mobile", loginAcct));
        if (entity == null){
            return null;
        }else {
            String passwordDb = entity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, passwordDb);
            if (matches){
                return entity;
            }else {
                return null;
            }
        }
    }

}