package com.awesome.kuibuservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.awesome.kuibuservice.mapper.TaskInfoMapper;
import com.awesome.kuibuservice.model.entity.TaskInfo;
import com.awesome.kuibuservice.service.KuibuInfoService;
import com.awesome.kuibuservice.service.TaskInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 82611
 * @description 针对表【task_info】的数据库操作Service实现
 * @createDate 2025-03-20 13:33:36
 */
@Service
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo>
        implements TaskInfoService {

    @Resource
    private KuibuInfoService kuibuInfoService;

    @Override
    @Transactional
    public void removeByTaskId(Long taskId, Long userId) {
        // 删除任务数据
        this.baseMapper.delete(Wrappers.<TaskInfo>lambdaQuery().eq(TaskInfo::getId, taskId).eq(TaskInfo::getUserId, userId));
        // 删除跬步数据
        kuibuInfoService.removeByTaskIdList(Collections.singletonList(taskId), userId);
    }

    @Override
    @Transactional
    public void removeByGoalId(Long goalId, Long userId) {
        // 查询任务id列表
        List<TaskInfo> taskInfoList = this.lambdaQuery()
                .eq(TaskInfo::getGoalId, goalId)
                .eq(TaskInfo::getUserId, userId)
                .list();
        List<Long> taskIds = taskInfoList.stream().map(TaskInfo::getId).collect(Collectors.toList());
        // 删除任务数据
        this.baseMapper.delete(Wrappers.<TaskInfo>lambdaQuery().eq(TaskInfo::getGoalId, goalId).eq(TaskInfo::getUserId, userId));
        // 删除跬步数据
        if (!CollectionUtil.isEmpty(taskIds)) {
            kuibuInfoService.removeByTaskIdList(taskIds, userId);
        }
    }
}




