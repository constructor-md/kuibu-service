package com.awesome.kuibuservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 什么时间 什么任务 完成什么类型数据
 */
@Data
public class NoticeDto {

    private String taskName;
    private Integer taskType;
    private Long count;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="Asia/Shanghai")
    private Date createTime;

}
