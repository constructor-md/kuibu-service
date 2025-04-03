package com.awesome.kuibuservice.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import com.awesome.kuibuservice.annotation.Login;
import com.awesome.kuibuservice.aop.UserInfoContext;
import com.awesome.kuibuservice.commons.R;
import com.awesome.kuibuservice.exception.ErrorCode;
import com.awesome.kuibuservice.exception.ThrowUtils;
import com.awesome.kuibuservice.model.dto.NoticeDto;
import com.awesome.kuibuservice.model.dto.TaskInfoAddRequest;
import com.awesome.kuibuservice.model.dto.TaskInfoDto;
import com.awesome.kuibuservice.model.dto.TaskInfoUpdateRequest;
import com.awesome.kuibuservice.model.entity.KuibuInfo;
import com.awesome.kuibuservice.model.entity.TaskInfo;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.awesome.kuibuservice.model.enums.GoalAndTaskStatusEnum;
import com.awesome.kuibuservice.service.KuibuInfoService;
import com.awesome.kuibuservice.service.TaskInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskInfoService taskInfoService;

    @Resource
    private KuibuInfoService kuibuInfoService;

    /**
     * 获取指定目标的任务列表
     */
    @Login
    @GetMapping("/list")
    public R<List<TaskInfoDto>> getTaskInfoListByGoalId(@RequestParam("id") String goalId) {
        UserInfo userInfo = UserInfoContext.get();
        List<TaskInfo> taskInfoList = taskInfoService.lambdaQuery()
                .eq(TaskInfo::getGoalId, Long.valueOf(goalId))
                .eq(TaskInfo::getUserId, userInfo.getId())
                .orderByDesc(TaskInfo::getCreateTime)
                .list();
        return R.ok(taskInfoList.stream().map(TaskInfoDto::transferDto).collect(Collectors.toList()));
    }

    /**
     * 为某个目标新建任务
     */
    @Login
    @PostMapping("/info")
    public R<?> addTask(@RequestBody TaskInfoAddRequest taskInfoAddRequest) {
        UserInfo userInfo = UserInfoContext.get();
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setName(taskInfoAddRequest.getName());
        taskInfo.setStatus(GoalAndTaskStatusEnum.ONGOING.getStatus());
        taskInfo.setType(taskInfoAddRequest.getType());
        taskInfo.setGoalId(Long.valueOf(taskInfoAddRequest.getGoalId()));
        taskInfo.setTargetAmount(taskInfoAddRequest.getAmount());
        taskInfo.setUserId(userInfo.getId());
        taskInfo.setCurrentProgress(0L);
        taskInfo.setCreateTime(new Date());
        taskInfo.setUpdateTime(new Date());
        taskInfoService.save(taskInfo);
        return R.ok();
    }

    /**
     * 修改任务信息
     */
    @Login
    @PutMapping("/info")
    public R<?> updateTask(@RequestBody TaskInfoUpdateRequest taskInfoUpdateRequest) {
        UserInfo userInfo = UserInfoContext.get();
        TaskInfo taskInfo = taskInfoService.lambdaQuery()
                .eq(TaskInfo::getId, taskInfoUpdateRequest.getId())
                .eq(TaskInfo::getGoalId, Long.valueOf(taskInfoUpdateRequest.getGoalId()))
                .eq(TaskInfo::getUserId, userInfo.getId())
                .one();
        taskInfo.setName(taskInfoUpdateRequest.getName());
        taskInfo.setTargetAmount(taskInfoUpdateRequest.getAmount());
        boolean result = taskInfoService.updateById(taskInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATOR_ERROR, "更新任务信息失败");
        return R.ok();
    }

    /**
     * 删除任务信息
     */
    @Login
    @DeleteMapping("/info")
    public R<?> deleteTask(@RequestParam("id") String taskId) {
        UserInfo userInfo = UserInfoContext.get();
        taskInfoService.removeByTaskId(Long.valueOf(taskId), userInfo.getId());
        return R.ok();
    }

    /**
     * 用户进行中的任务总数
     */
    @Login
    @GetMapping("/ongoing/count")
    public R<Long> getOngoingTaskCount() {
        UserInfo userInfo = UserInfoContext.get();
        Long count = taskInfoService.lambdaQuery()
                .eq(TaskInfo::getUserId, userInfo.getId())
                .eq(TaskInfo::getStatus, GoalAndTaskStatusEnum.ONGOING.getStatus())
                .count();
        return R.ok(count);
    }

    /**
     * 用户已完成的任务总数
     */
    @Login
    @GetMapping("/completed/count")
    public R<Long> getCompletedTaskCount() {
        UserInfo userInfo = UserInfoContext.get();
        Long count = taskInfoService.lambdaQuery()
                .eq(TaskInfo::getUserId, userInfo.getId())
                .eq(TaskInfo::getStatus, GoalAndTaskStatusEnum.COMPLETE.getStatus())
                .count();
        return R.ok(count);
    }

    /**
     * 任务公告
     * 用户最近完成的跬步的五个任务信息和跬步信息
     */
    @Login
    @GetMapping("/notice")
    public R<List<NoticeDto>> getNoticeDtoList() {
        UserInfo userInfo = UserInfoContext.get();

        // 获取用户最新的五条跬步数据
        QueryWrapper<KuibuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotEmpty(userInfo.getId()), "user_id", userInfo.getId());
        queryWrapper.orderBy(true, false, "create_time");
        Page<KuibuInfo> kuibuInfoPage = kuibuInfoService.page(new Page<>(1L, 5L), queryWrapper);
        List<Long> taskIds = kuibuInfoPage.getRecords().stream().map(KuibuInfo::getTaskId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(taskIds)) {
            return R.ok(new ArrayList<>());
        }
        // 反查任务信息
        List<TaskInfo> taskInfoList = taskInfoService.lambdaQuery()
                .in(TaskInfo::getId, taskIds)
                .eq(TaskInfo::getUserId, userInfo.getId())
                .list();
        Map<Long, TaskInfo> id2Task = taskInfoList.stream().collect(Collectors.toMap(TaskInfo::getId, d -> d, (old, now) -> now));
        if (CollectionUtil.isEmpty(taskInfoList)) {
            return R.ok(new ArrayList<>());
        }

        // 整理数据
        List<NoticeDto> noticeDtoList = new ArrayList<>();
        kuibuInfoPage.getRecords().forEach(kuibuInfo -> {
            NoticeDto noticeDto = new NoticeDto();
            TaskInfo taskInfo = id2Task.get(kuibuInfo.getTaskId());
            if (taskInfo != null) {
                noticeDto.setTaskName(taskInfo.getName());
                noticeDto.setTaskType(taskInfo.getType());
                noticeDto.setCount(kuibuInfo.getCount());
                noticeDto.setCreateTime(kuibuInfo.getCreateTime());
            }
            noticeDtoList.add(noticeDto);
        });

        return R.ok(noticeDtoList);

    }

}
