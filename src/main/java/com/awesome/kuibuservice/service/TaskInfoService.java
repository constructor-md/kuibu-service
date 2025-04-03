package com.awesome.kuibuservice.service;

import com.awesome.kuibuservice.model.entity.TaskInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 82611
* @description 针对表【task_info】的数据库操作Service
* @createDate 2025-03-20 13:33:36
*/
public interface TaskInfoService extends IService<TaskInfo> {

    void removeByTaskId(Long taskId, Long userId);

    void removeByGoalId(Long goalId, Long userId);

}
