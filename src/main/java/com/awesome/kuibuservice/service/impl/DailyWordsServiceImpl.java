package com.awesome.kuibuservice.service.impl;

import com.awesome.kuibuservice.model.entity.DailyWords;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.awesome.kuibuservice.service.DailyWordsService;
import com.awesome.kuibuservice.mapper.DailyWordsMapper;
import org.springframework.stereotype.Service;

/**
* @author 82611
* @description 针对表【daily_words】的数据库操作Service实现
* @createDate 2025-03-20 13:33:00
*/
@Service
public class DailyWordsServiceImpl extends ServiceImpl<DailyWordsMapper, DailyWords>
    implements DailyWordsService{

}




