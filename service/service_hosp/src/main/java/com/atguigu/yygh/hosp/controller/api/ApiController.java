package com.atguigu.yygh.hosp.controller.api;


import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api("医院相关操作")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Resource
    private HospitalService hospitalService;
    @Resource
    private HospitalSetService hospitalSetService;
    @Resource
    private DepartmentService departmentService;
    @Resource
    private ScheduleService scheduleService;

    @ApiOperation("医院端提交JSON数据后写入MongoDB")
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        boolean flag = verifySign(map);

        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoDataString = (String) map.get("logoData");
        if (!StringUtils.isEmpty(logoDataString)) {
            String logoData = logoDataString.replaceAll(" ", "+");
            map.put("logoData", logoData);
        }

        this.hospitalService.save(map);
        return Result.ok();
    }

    @ApiOperation("医院端访问这个接口查询该医院的信息")
    @PostMapping("/hospital/show")
    public Result show(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        boolean flag = verifySign(map);

        if (!flag) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        // 从MongoDB查询信息并返回
        Hospital hospital = this.hospitalService.getHospitalByHoscode(map.get("hoscode").toString());

        return Result.ok(hospital);
    }

    @ApiOperation("保存科室信息")
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        boolean flag = verifySign(map);

        if (!flag) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 保存科室信息
        this.departmentService.saveDepartment(map);
        return Result.ok();
    }

    @ApiOperation("查询科室信息接口")
    @PostMapping("/department/list")
    public Result getDeptList(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        boolean flag = verifySign(map);

        if (!flag) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 保存科室信息
        Page<Department> pageModel = this.departmentService.getDeptList(map);
        return Result.ok(pageModel);
    }

    @ApiOperation("删除科室信息")
    @PostMapping("/department/remove")
    public Result deleteDept(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        boolean flag = verifySign(map);

        if (!flag) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 保存科室信息
        this.departmentService.deleteDept(map);
        return Result.ok();
    }

    @ApiOperation("添加排班信息")
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        boolean flag = verifySign(map);

        if (!flag) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 保存排班信息
        this.scheduleService.saveSchedule(map);
        return Result.ok();
    }

    @ApiOperation("查询排班信息")
    @PostMapping("/schedule/list")
    public Result getScheduleList(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        boolean flag = verifySign(map);

        if (!flag) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 保存排班信息
        Page<Schedule> pageModel =  this.scheduleService.getScheduleList(map);
        return Result.ok(pageModel);
    }

    @ApiOperation("删除排班信息")
    @PostMapping("/schedule/remove")
    public Result deleteSchedule(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        boolean flag = verifySign(map);

        if (!flag) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        this.scheduleService.deleteSchedule(map);
        return Result.ok();
    }

    private boolean verifySign(Map<String, Object> map) {
        String sign = map.get("sign").toString();
        String hoscode = map.get("hoscode").toString();
        // 判断一下签名是否一致
        return this.hospitalSetService.checkSign(sign, hoscode);
    }
}
