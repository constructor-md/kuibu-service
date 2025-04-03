package com.awesome.kuibuservice.service.impl;

import com.awesome.kuibuservice.mapper.GoalInfoMapper;
import com.awesome.kuibuservice.model.entity.GoalInfo;
import com.awesome.kuibuservice.service.GoalInfoService;
import com.awesome.kuibuservice.service.TaskInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 82611
 * @description 针对表【goal_info】的数据库操作Service实现
 * @createDate 2025-03-20 13:33:21
 */
@Service
public class GoalInfoServiceImpl extends ServiceImpl<GoalInfoMapper, GoalInfo>
        implements GoalInfoService {

    @Resource
    private TaskInfoService taskInfoService;

    @Override
    @Transactional
    public boolean removeByGoalId(Long goalId, Long userId) {
        // 删除目标数据
        int delete1 = this.baseMapper.delete(Wrappers.<GoalInfo>lambdaQuery().eq(GoalInfo::getId, goalId).eq(GoalInfo::getUserId, userId));
        // 删除任务和跬步数据
        taskInfoService.removeByGoalId(goalId, userId);
        return delete1 > 0;
    }
}




