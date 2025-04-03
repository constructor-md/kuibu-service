package com.awesome.kuibuservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.awesome.kuibuservice.model.entity.UserSuggestion;
import com.awesome.kuibuservice.service.UserSuggestionService;
import com.awesome.kuibuservice.mapper.UserSuggestionMapper;
import org.springframework.stereotype.Service;

/**
* @author 82611
* @description 针对表【user_suggestion】的数据库操作Service实现
* @createDate 2025-03-25 10:15:51
*/
@Service
public class UserSuggestionServiceImpl extends ServiceImpl<UserSuggestionMapper, UserSuggestion>
    implements UserSuggestionService{

}




