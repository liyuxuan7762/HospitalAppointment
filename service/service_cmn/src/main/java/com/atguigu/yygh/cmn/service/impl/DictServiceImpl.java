package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Resource
    private DictMapper dictMapper;

    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    @Override
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id", id);
        List<Dict> dictList = super.list(queryWrapper);
        for (Dict dict : dictList) {
            dict.setHasChildren(hasChild(dict.getId()));
        }
        return dictList;

        // 测试
    }

    @Override
    public void exportData(HttpServletResponse response) {
        // 设置相关参数
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 查询到所有的dict
        List<Dict> list = super.list();
        // 将dict转化成dictVO
        List<DictEeVo> dictEeVoList = new ArrayList<>(list.size());

        DictEeVo dictEeVo;
        for (Dict dict : list) {
            dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);
            dictEeVoList.add(dictEeVo);
        }

        // 保存到excel
        try {
            EasyExcel.write(response.getOutputStream()).sheet("数据字典").doWrite(dictEeVoList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @CacheEvict(value = "dict", allEntries = true)
    public void importData(MultipartFile file) {
        // 读取文件
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(dictMapper)).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName(String dictCode, String value) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        Dict one = super.getOne(queryWrapper);
        if (one == null) {
            return null;
        }
        Dict dict = super.getOne(new QueryWrapper<Dict>()
                .eq("parent_id", one.getId())
                .eq("value", value)

        );
        return dict.getName();
    }

    @Override
    public String getName(String value) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("value", value);
        Dict one = this.getOne(queryWrapper);
        if (one != null) {
            return one.getName();
        }
        return null;
    }

    @Override
    public List<Dict> getProvidenceList() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", "Province");
        Dict one = super.getOne(queryWrapper);
        if (one == null) {
            return null;
        }
        return super.list(new QueryWrapper<Dict>()
                .eq("parent_id", one.getId())
        );
    }

    @Override
    public List<Dict> getCityList(Integer code) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", code);
        return super.list(queryWrapper);
    }

    private boolean hasChild(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id", id);
        int count = super.count(queryWrapper);
        return count > 0;
    }
}
