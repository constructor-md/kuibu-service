package com.awesome.kuibuservice.service;

import com.awesome.kuibuservice.model.dto.DailyWordsDto;
import com.awesome.kuibuservice.model.entity.DailyWords;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 82611
* @description 针对表【daily_words】的数据库操作Service
* @createDate 2025-03-20 13:33:00
*/
public interface DailyWordsService extends IService<DailyWords> {

    DailyWordsDto getRandomOneSentence();

}
