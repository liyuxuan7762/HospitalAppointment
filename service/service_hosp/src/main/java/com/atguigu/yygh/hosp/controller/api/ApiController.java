package com.atguigu.yygh.hosp.controller.api;


import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.Hospital;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation("医院端提交JSON数据后写入MongoDB")
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        String sign = map.get("sign").toString();
        String hoscode = map.get("hoscode").toString();
        // 判断一下签名是否一致
        boolean flag = this.hospitalSetService.checkSign(sign, hoscode);

        if (!flag) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoDataString = (String)map.get("logoData");
        if(!StringUtils.isEmpty(logoDataString)) {
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

        String sign = map.get("sign").toString();
        String hoscode = map.get("hoscode").toString();
        // 判断一下签名是否一致
        boolean flag = this.hospitalSetService.checkSign(sign, hoscode);

        if (!flag) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 从MongoDB查询信息并返回
        Hospital hospital = this.hospitalService.getHospitalByHoscode(hoscode);

        return Result.ok(hospital);
    }
}
