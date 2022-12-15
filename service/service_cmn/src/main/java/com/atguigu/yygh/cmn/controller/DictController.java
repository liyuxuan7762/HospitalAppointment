package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api("数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class DictController {
    @Resource
    private DictService dictService;

    @ApiOperation("根据ID查询Dict的所有子数据")
    @GetMapping("/findChildData/{id}")
    public Result findChildData(@PathVariable(name = "id") Long id) {
        List<Dict> dictList = this.dictService.findChildData(id);
        return  Result.ok(dictList);
    }

    @ApiOperation("导出所有数据到excel")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response) {
        this.dictService.exportData(response);
    }

    @ApiOperation("从Excel中导入文件")
    @PostMapping("/importData")
    public void importData(MultipartFile file) {
        this.dictService.importData(file);
    }

    @ApiOperation("根据dict中的value查询对应的值")
    @GetMapping("/getName/{value}")
    public String getName(@PathVariable(name = "value") String value) {
        return this.dictService.getName(value);
    }

    @ApiOperation("根据dict中的value和dict_code查询对应的值")
    @GetMapping("/getName/{dictCode}/{value}")
    public String getName(@PathVariable(name = "dictCode") String dictCode, @PathVariable(name = "value") String value) {
        return this.dictService.getName(dictCode, value);
    }

    @ApiOperation("查询所有省份信息")
    @GetMapping("/providenceList")
    public Result getProvidenceList() {
        List<Dict> providenceList = this.dictService.getProvidenceList();
        return Result.ok(providenceList);
    }

    @ApiOperation("根据省份查询对应市")
    @GetMapping("/getCity/{code}")
    public Result getCity(@PathVariable(name = "code") Integer code) {
        List<Dict> cityList = this.dictService.getCityList(code);
        return Result.ok(cityList);
    }

}
