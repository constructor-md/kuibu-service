package com.awesome.kuibuservice.service;

import com.awesome.kuibuservice.model.entity.GoalInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 82611
* @description 针对表【goal_info】的数据库操作Service
* @createDate 2025-03-20 13:33:21
*/
public interface GoalInfoService extends IService<GoalInfo> {

    boolean removeByGoalId(Long goalId, Long userId);

}
