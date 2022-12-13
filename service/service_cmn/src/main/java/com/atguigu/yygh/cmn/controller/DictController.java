package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    // @Cacheable(value = "dict", keyGenerator = "keyGenerator")
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
    @CacheEvict(value = "dict", allEntries = true)
    public void importData(MultipartFile file) {
        this.dictService.importData(file);
    }



}
