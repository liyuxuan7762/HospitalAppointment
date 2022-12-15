package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api("医院管理接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {

    @Resource
    private HospitalService hospitalService;

    @ApiOperation("医院信息分页查询")
    @GetMapping("/list/{page}/{limit}")
    public Result getHospList(@PathVariable(name = "page") Integer page,
                              @PathVariable(name = "limit") Integer limit,
                              HospitalQueryVo hospitalQueryVo) {
        // 根据条件到MongoDB中查询，并分页 返回值应该是一个Page对象
        Page<Hospital> pageModel = this.hospitalService.getHospList(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation("更新医院状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable(name = "id") String id,
                               @PathVariable(name = "status") Integer status) {
        this.hospitalService.updateStatus(id, status);
        return Result.ok();
    }
}
