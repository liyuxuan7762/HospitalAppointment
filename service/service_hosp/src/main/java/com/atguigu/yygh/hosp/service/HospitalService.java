package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> map);

    Hospital getHospitalByHoscode(String hoscode);
}
