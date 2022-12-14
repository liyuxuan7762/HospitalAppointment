package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {
    void saveSchedule(Map<String, Object> map);

    Page<Schedule> getScheduleList(Map<String, Object> map);

    void deleteSchedule(Map<String, Object> map);
}
