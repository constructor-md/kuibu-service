package com.awesome.kuibuservice.controller;

import cn.hutool.core.util.NumberUtil;
import com.awesome.kuibuservice.annotation.Login;
import com.awesome.kuibuservice.commons.R;
import com.awesome.kuibuservice.model.dto.BarchartDto;
import com.awesome.kuibuservice.model.dto.KuibuInfoRequest;
import com.awesome.kuibuservice.model.entity.TaskInfo;
import com.awesome.kuibuservice.service.KuibuInfoService;
import com.awesome.kuibuservice.service.TaskInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/kuibu")
public class KuibuController {

    @Resource
    private KuibuInfoService kuibuInfoService;

    @Resource
    private TaskInfoService taskInfoService;

    /**
     * 跬步记录
     */
    @Login
    @PostMapping("/info")
    public R<?> addRecords(@RequestBody KuibuInfoRequest kuibuInfoRequest) {
        kuibuInfoService.addRecord(kuibuInfoRequest);
        return R.ok();
    }


    /**
     * 指定任务最近一周跬步
     * 纵向柱状图
     */
    @Login
    @GetMapping("/histogram/task")
    public R<BarchartDto> getTaskBarChart(@RequestParam("id") String taskId) {
        BarchartDto barchartDto = kuibuInfoService.getTaskBarChart(Long.valueOf(taskId));
        return R.ok(barchartDto);
    }


    /**
     * 指定目标的所有任务进度情况
     * 横向柱状图
     */
    @Login
    @GetMapping("/histogram/goal")
    public R<BarchartDto> getGoalBarChart(@RequestParam("id") String goalId) {

        // 查找所有任务
        List<TaskInfo> taskInfoList = taskInfoService.lambdaQuery().eq(TaskInfo::getGoalId, goalId).list();
        BarchartDto barchartDto = new BarchartDto();
        taskInfoList.forEach(task -> {
            barchartDto.getItems().add(task.getName());
            double percent = NumberUtil.round(task.getCurrentProgress() * 100.0 / task.getTargetAmount(), 2).doubleValue();
            percent = percent > 100 ? 100 : percent;
            barchartDto.getValues().add(percent);
        });
        return R.ok(barchartDto);
    }


}
