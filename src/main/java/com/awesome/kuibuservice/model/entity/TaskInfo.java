package com.awesome.kuibuservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName task_info
 */
@TableName(value ="task_info")
@Data
public class TaskInfo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 任务类型 1 - 计时 2 - 计数
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 所属目标id
     */
    @TableField(value = "goal_id")
    private Long goalId;

    /**
     * 任务达成所需的量
     */
    @TableField(value = "target_amount")
    private Long targetAmount;

    /**
     * 任务当前达成的量
     */
    @TableField(value = "current_progress")
    private Long currentProgress;

    /**
     * 任务状态 0 - 进行中 1 - 已完成
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 所属用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 记录更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}