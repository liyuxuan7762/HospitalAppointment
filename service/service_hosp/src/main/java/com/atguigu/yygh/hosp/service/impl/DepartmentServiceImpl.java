package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentRepository departmentRepository;


    @Override
    public void saveDepartment(Map<String, Object> map) {

        // 这里医院端解析JSON，得到科室列表，然后循环遍历列表，发送请求
        // 每一次请求发送一个科室信息
        // 医院端代码写的真垃圾，有100个科室需要发送100次请求，效率低到极点

        String jsonString = JSONObject.toJSONString(map);
        Department department = JSONObject.parseObject(jsonString, Department.class);
        Department target = isDepartmentExists(department.getHoscode(), department.getDepcode());

        // 根据target是否为空判断是添加还是修改
        if (target != null) {
            // 修改
            department.setId(target.getId());
            department.setCreateTime(target.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            this.departmentRepository.save(department);
        } else {
            // 添加
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            this.departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> getDeptList(Map<String, Object> map) {
        // 获取医院编号，根据医院编号查询对应科室信息
        String hoscode = map.get("hoscode").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int limit = Integer.parseInt(map.get("limit").toString());

        Pageable pageable = PageRequest.of(page - 1, limit);
        Example<Department> example = Example.of(new Department(hoscode));

        return departmentRepository.findAll(example, pageable);
    }

    @Override
    public void deleteDept(Map<String, Object> map) {
        String hoscode = map.get("hoscode").toString();
        String depcode = map.get("depcode").toString();

        // 下查询一下是否存在
        Example<Department> example = Example.of(new Department(hoscode, depcode));
        Optional<Department> one = departmentRepository.findOne(example);
        if (one.isPresent()) {
            //  存在
            Department department = one.get();
            departmentRepository.deleteById(department.getId());
        }
    }

    private Department isDepartmentExists(String hoscode, String depcode) {
        Example<Department> example = Example.of(new Department(hoscode, depcode));
        Optional<Department> one = departmentRepository.findOne(example);
        if (one.isPresent()) {
            return one.get();
        }
        return null;
    }
}
