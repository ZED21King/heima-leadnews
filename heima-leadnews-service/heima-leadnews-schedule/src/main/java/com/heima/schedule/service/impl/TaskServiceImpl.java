package com.heima.schedule.service.impl;

import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public Long addTask(Task task) {
        //先将任务存入MySQL数据
        if(addTaskToDb(task)){
            //再将任务存入Redis缓存
            addTaskToCache(task);
        }
        //返回任务ID
        return task.getTaskId();
    }

    /**
     * 将任务存入Redis缓存
     * @param task
     */
    private void addTaskToCache(Task task) {
        //需求：只有在Task任务的执行时间小于或等于未来5分钟的任务，才存入redis


    }

    /**
     * 先将任务存入MySQL数据
     * @param task
     */
    private boolean addTaskToDb(Task task) {
        boolean flag = false;

        return flag;
    }
}