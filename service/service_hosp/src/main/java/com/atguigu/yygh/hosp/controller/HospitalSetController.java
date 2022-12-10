package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api("医院信息管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Resource
    private HospitalSetService hospitalSetService;

    @ApiOperation("查询所有医院信息")
    @GetMapping("/findAll")
    public List<HospitalSet> getAllHospitals () {
        return this.hospitalSetService.list();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据ID逻辑删除医院信息 ")
    public void deleteById(@PathVariable(name = "id") Long id) {
        this.hospitalSetService.removeById(id);
    }
}
