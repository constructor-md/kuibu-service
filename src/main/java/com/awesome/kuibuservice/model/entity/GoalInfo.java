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
 * @TableName goal_info
 */
@TableName(value ="goal_info")
@Data
public class GoalInfo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 目标名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 目标状态 0 - 进行中 1 - 已完成
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