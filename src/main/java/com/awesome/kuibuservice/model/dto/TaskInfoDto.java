package com.awesome.kuibuservice.model.dto;

import cn.hutool.core.util.NumberUtil;
import com.awesome.kuibuservice.model.entity.TaskInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class TaskInfoDto {

    private String id;
    private String name;
    private Integer type;
    private String goalId;
    private Double percent;
    private Integer status;
    private Long amount;

    public static TaskInfoDto transferDto(TaskInfo taskInfo) {
        TaskInfoDto taskInfoDto = new TaskInfoDto();
        BeanUtils.copyProperties(taskInfo, taskInfoDto);
        taskInfoDto.setId(String.valueOf(taskInfo.getId()));
        taskInfoDto.setGoalId(String.valueOf(taskInfo.getGoalId()));
        double percent = NumberUtil.round(taskInfo.getCurrentProgress() * 1.0 / taskInfo.getTargetAmount(), 2).doubleValue();
        taskInfoDto.setPercent(percent);
        taskInfoDto.setAmount(taskInfo.getTargetAmount());
        return taskInfoDto;
    }

}
