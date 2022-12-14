package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Resource
    private ScheduleRepository scheduleRepository;

    @Override
    public void saveSchedule(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Schedule schedule = JSONObject.parseObject(jsonString, Schedule.class);
        Schedule target = isScheduleExists(schedule.getHoscode(), schedule.getHosScheduleId());

        // 根据target是否为空判断是添加还是修改
        if (target != null) {
            // 修改
            schedule.setId(target.getId());
            schedule.setCreateTime(target.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            this.scheduleRepository.save(schedule);
        } else {
            // 添加
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            this.scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> getScheduleList(Map<String, Object> map) {
        String hoscode = map.get("hoscode").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int limit = Integer.parseInt(map.get("limit").toString());

        Pageable pageable = PageRequest.of(page - 1, limit);
        Example<Schedule> example = Example.of(new Schedule(hoscode));

        return scheduleRepository.findAll(example, pageable);
    }

    @Override
    public void deleteSchedule(Map<String, Object> map) {
        String hoscode = map.get("hoscode").toString();
        String hosScheduleId = map.get("hosScheduleId").toString();

        // 下查询一下是否存在
        Example<Schedule> example = Example.of(new Schedule(hoscode, hosScheduleId));
        Optional<Schedule> one = scheduleRepository.findOne(example);
        if (one.isPresent()) {
            //  存在
            Schedule schedule = one.get();
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    private Schedule isScheduleExists(String hoscode, String scheduleId) {
        Example<Schedule> example = Example.of(new Schedule(hoscode, scheduleId));
        Optional<Schedule> one = scheduleRepository.findOne(example);
        if (one.isPresent()) {
            return one.get();
        }
        return null;
    }
}
