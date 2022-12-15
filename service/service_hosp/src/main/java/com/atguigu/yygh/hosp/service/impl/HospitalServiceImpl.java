package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Resource
    private HospitalRepository hospitalRepository;

    @Resource
    private DictFeignClient dictFeignClient;

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
                hospital.setCreateTime(target.getCreateTime());
                hospital.setUpdateTime(new Date());
                hospital.setStatus(target.getStatus());
                hospital.setIsDeleted(0);
                hospitalRepository.save(hospital);
            } else {
                // 添加
                hospital.setCreateTime(new Date());
                hospital.setUpdateTime(new Date());
                hospital.setIsDeleted(0);
                hospital.setStatus(0);
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

    @Override
    public Page<Hospital> getHospList(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Hospital> pageModel = null;
        if (hospitalQueryVo != null) {
            // 构建条件
            Hospital hospital = new Hospital();
            BeanUtils.copyProperties(hospitalQueryVo, hospital);
            ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                    .withIgnoreCase(true);

            Example<Hospital> example = Example.of(hospital, matcher);

            // TODO 还需要根据医院级别代码以及地区代码到相应的数据字典中查询出来对应的值然后封装
            pageModel = this.hospitalRepository.findAll(example, pageable);
        } else {
            pageModel = this.hospitalRepository.findAll(pageable);
        }


        for (Hospital h : pageModel.getContent()) {
            // 每一个医院对应都取出对应的code信息，然后通过Feign远程调用访问数据字典，
            // 将查询到数据封装到医院对象的paramMap中
            String hostypeName = this.dictFeignClient.getName("Hostype", h.getHostype());
            String cityName = this.dictFeignClient.getName(h.getCityCode());
            String districtName = this.dictFeignClient.getName(h.getDistrictCode());
            String provinceName = this.dictFeignClient.getName(h.getProvinceCode());

            Map<String, Object> param = h.getParam();
            param.put("hostypeName", hostypeName);
            param.put("cityName", cityName);
            param.put("fullAddress", provinceName + districtName);
        }

        return pageModel;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = this.hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        this.hospitalRepository.save(hospital);
    }
}
