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
 * @TableName kuibu_info
 */
@TableName(value ="kuibu_info")
@Data
public class KuibuInfo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属任务id
     */
    @TableField(value = "task_id")
    private Long taskId;

    /**
     * 次数 / 时间毫秒数
     */
    @TableField(value = "count")
    private Long count;

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