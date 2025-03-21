package com.awesome.kuibuservice.service.impl;

import com.awesome.kuibuservice.model.entity.TaskInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.awesome.kuibuservice.service.TaskInfoService;
import com.awesome.kuibuservice.mapper.TaskInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author 82611
* @description 针对表【task_info】的数据库操作Service实现
* @createDate 2025-03-20 13:33:36
*/
@Service
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo>
    implements TaskInfoService{

}




