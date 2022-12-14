package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void saveDepartment(Map<String, Object> map);

    Page<Department> getDeptList(Map<String, Object> map);

    void deleteDept(Map<String, Object> map);
}
