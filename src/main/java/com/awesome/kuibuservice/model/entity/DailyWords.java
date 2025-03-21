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
 * @TableName daily_words
 */
@TableName(value ="daily_words")
@Data
public class DailyWords implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 句子
     */
    @TableField(value = "sentence")
    private String sentence;

    /**
     * 作者
     */
    @TableField(value = "author")
    private String author;

    /**
     * 来源
     */
    @TableField(value = "source")
    private String source;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}