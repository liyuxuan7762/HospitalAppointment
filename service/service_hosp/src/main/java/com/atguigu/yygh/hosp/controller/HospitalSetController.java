package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Api("医院设置管理接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin
public class HospitalSetController {
    @Resource
    private HospitalSetService hospitalSetService;

    @ApiOperation("查询所有医院信息")
    @GetMapping("/findAll")
    public Result getAllHospitals() {
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("根据ID逻辑删除医院信息 ")
    public Result deleteById(@PathVariable(name = "id") Long id) {
        boolean result = this.hospitalSetService.removeById(id);
        if (result) {
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("根据医院名称或者编号模糊分页查询")
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable(name = "current") Long current,
                                  @PathVariable(name = "limit") Long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo
    ) {
        Page<HospitalSet> page = new Page<>(current, limit);

        String hoscode = hospitalSetQueryVo.getHoscode();
        String hosname = hospitalSetQueryVo.getHosname();
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hoscode)) {
            queryWrapper.eq("hoscode", hoscode);
        }
        if (!StringUtils.isEmpty(hosname)) {
            queryWrapper.like("hosname", hosname);
        }

        Page<HospitalSet> result = hospitalSetService.page(page, queryWrapper);

        return Result.ok(result);
    }

    @ApiOperation("添加医院信息")
    @PostMapping("/saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        // 设置状态码 1可用 0不可用
        hospitalSet.setStatus(1);
        // 设置链接秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        return save ? Result.ok() : Result.fail();
    }

    @ApiOperation("根据ID查询医院信息")
    @GetMapping("getHospSet/{id}")
    @CrossOrigin
    public Result getHospSet(@PathVariable(name = "id") Long id) {
        HospitalSet hospitalSet = this.hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    @ApiOperation("根据ID修改医院信息")
    @PostMapping("/updateHospSet")
    @CrossOrigin
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet) {
        boolean result = this.hospitalSetService.updateById(hospitalSet);
        return result ? Result.ok() : Result.fail();
    }

    @ApiOperation("根据ID批量删除")
    @DeleteMapping("/batchDelete")
    public Result batchDelete(@RequestBody List<Long> ids) {
        this.hospitalSetService.removeByIds(ids);
        return Result.ok();
    }

    @ApiOperation("设置医院状态信息")
    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable(name = "id") Long id,
                                  @PathVariable(name = "status") Integer status) {
        HospitalSet hospitalSet = this.hospitalSetService.getById(id);
        if (hospitalSet == null) {
            return Result.fail();
        }
        hospitalSet.setStatus(status);
        this.hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }


    @ApiOperation("发送指定ID医院的秘钥")
    @PutMapping("/sendKey/{id}")
    public Result sendKey(@PathVariable(name = "id") Long id) {
        HospitalSet hospitalSet = this.hospitalSetService.getById(id);
        if (hospitalSet == null) {
            return Result.fail();
        }
        String signKey = hospitalSet.getSignKey();

        // TODO 通过短信发送秘钥

        return Result.ok();
    }

}
