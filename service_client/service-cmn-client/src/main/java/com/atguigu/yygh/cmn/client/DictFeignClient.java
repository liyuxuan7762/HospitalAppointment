package com.atguigu.yygh.cmn.client;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
public interface DictFeignClient {
    @ApiOperation("根据dict中的value查询对应的值")
    @GetMapping("/admin/cmn/dict/getName/{value}")
    String getName(@PathVariable(name = "value") String value);


    @ApiOperation("根据dict中的value和dict_code查询对应的值")
    @GetMapping("/admin/cmn/dict/getName/{dictCode}/{value}")
    String getName(@PathVariable(name = "dictCode") String dictCode, @PathVariable(name = "value") String value);


}
