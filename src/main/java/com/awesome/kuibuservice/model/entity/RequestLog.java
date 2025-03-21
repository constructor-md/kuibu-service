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
 * @TableName request_log
 */
@TableName(value ="request_log")
@Data
public class RequestLog implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 请求路径
     */
    @TableField(value = "url")
    private String url;

    /**
     * 请求方法
     */
    @TableField(value = "method")
    private String method;

    /**
     * 请求头 JSON
     */
    @TableField(value = "headers")
    private String headers;

    /**
     * 请求体 JSON
     */
    @TableField(value = "body")
    private String body;

    /**
     * 请求是否成功 0 - 成功 1- 失败
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 错误码
     */
    @TableField(value = "error_code")
    private Integer errorCode;

    /**
     * 错误信息
     */
    @TableField(value = "error_message")
    private String errorMessage;

    /**
     * 异常堆栈
     */
    @TableField(value = "stack_trace")
    private String stackTrace;

    /**
     * 客户端ip
     */
    @TableField(value = "client_ip")
    private String clientIp;

    /**
     * 客户端设备或浏览器信息
     */
    @TableField(value = "user_agent")
    private String userAgent;

    /**
     * 请求时间
     */
    @TableField(value = "request_time")
    private Date requestTime;

    /**
     * 响应时间
     */
    @TableField(value = "response_time")
    private Date responseTime;

    /**
     * 接口耗时
     */
    @TableField(value = "duration")
    private Long duration;

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

    /**
     * 请求用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}