package com.awesome.kuibuservice.service.impl;

import com.awesome.kuibuservice.model.entity.RequestLog;
import com.awesome.kuibuservice.service.RequestLogService;
import com.awesome.kuibuservice.mapper.RequestLogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author 82611
* @description 针对表【request_log】的数据库操作Service实现
* @createDate 2025-03-20 13:33:29
*/
@Service
public class RequestLogServiceImpl extends ServiceImpl<RequestLogMapper, RequestLog>
    implements RequestLogService{

}




