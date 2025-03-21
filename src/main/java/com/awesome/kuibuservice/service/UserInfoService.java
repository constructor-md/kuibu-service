package com.awesome.kuibuservice.service;

import com.awesome.kuibuservice.model.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 82611
* @description 针对表【user_info】的数据库操作Service
* @createDate 2025-03-20 13:33:38
*/
public interface UserInfoService extends IService<UserInfo> {

    String login(String code);

}
