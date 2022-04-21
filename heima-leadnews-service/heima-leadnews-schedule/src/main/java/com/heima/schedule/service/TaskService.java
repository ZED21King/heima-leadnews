package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

/**
 * 任务处理业务
 */
public interface TaskService {

    /**
     * 添加任务
     */
    Long addTask(Task task);
}