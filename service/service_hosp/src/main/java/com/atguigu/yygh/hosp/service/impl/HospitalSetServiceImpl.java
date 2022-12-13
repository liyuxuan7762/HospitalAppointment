package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    @Override
    public boolean checkSign(String sign, String hoscode) {
        // 根据hoscode查询对应的医院的sign
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = super.getOne(queryWrapper);
        if (hospitalSet == null) {
            return false;
        }
        String key = MD5.encrypt(hospitalSet.getSignKey());
        if(key.equals(sign)) {
            return true;
        }
        return false;
    }
}
