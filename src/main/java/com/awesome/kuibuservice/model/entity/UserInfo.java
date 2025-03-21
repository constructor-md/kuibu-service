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
 * @TableName user_info
 */
@TableName(value ="user_info")
@Data
public class UserInfo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 微信openid
     */
    @TableField(value = "open_id")
    private String openId;

    /**
     * 微信session_key
     */
    @TableField(value = "session_key")
    private String sessionKey;

    /**
     * 用户自定义名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 用户自定义头像
     */
    @TableField(value = "avatar")
    private String avatar;

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