package com.awesome.kuibuservice.model;

import lombok.Data;

/**
 * 分页请求包装类
 */
@Data
public class PageRequest {

    // 当前页号
    private int current = 1;
    // 页面大小
    private int pageSize = 10;
}
