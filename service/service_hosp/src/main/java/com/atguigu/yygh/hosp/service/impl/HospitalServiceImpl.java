package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Resource
    private HospitalRepository hospitalRepository;

    @Override
    public void save(Map<String, Object> map) {
        if (map != null) {
            // 将map转化为对象 使用hutool工具类可以使用fillBeanWithMap
            String mapStr = JSONObject.toJSONString(map);
            Hospital hospital = JSONObject.parseObject(mapStr, Hospital.class);

            // 根据hoscode判断这个医院是否已经保存到MongoDB中了
            Hospital target = getHospitalByHoscode(hospital.getHoscode());

            if (target != null) {
                // 修改
                hospital.setId(target.getId());
                hospitalRepository.save(hospital);
            } else {
                // 添加
                hospitalRepository.save(hospital);
            }
        }
    }



    // 根据hodcode到数据库中查询医院信息
    public Hospital getHospitalByHoscode(String hoscode) {
        Example<Hospital> example = Example.of(new Hospital(hoscode));
        Optional<Hospital> one = hospitalRepository.findOne(example);
        if (one.isPresent() == true) {
            // 如果存在
            return one.get();
        } else {
            return null;
        }
    }
}
