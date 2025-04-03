package com.awesome.kuibuservice.service.impl;

import com.awesome.kuibuservice.model.dto.DailyWordsDto;
import com.awesome.kuibuservice.model.entity.DailyWords;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.awesome.kuibuservice.service.DailyWordsService;
import com.awesome.kuibuservice.mapper.DailyWordsMapper;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
* @author 82611
* @description 针对表【daily_words】的数据库操作Service实现
* @createDate 2025-03-20 13:33:00
*/
@Service
public class DailyWordsServiceImpl extends ServiceImpl<DailyWordsMapper, DailyWords>
    implements DailyWordsService{

    @Override
    public DailyWordsDto getRandomOneSentence() {

        // 查询句子库id范围
        QueryWrapper<DailyWords> maxQuery = new QueryWrapper<>();
        maxQuery.select("max(id) as id");
        Long max = this.baseMapper.selectOne(maxQuery).getId();
        QueryWrapper<DailyWords> minQuery = new QueryWrapper<>();
        minQuery.select("min(id) as id");
        Long min = this.baseMapper.selectOne(minQuery).getId();

        // 生成范围内随机id
        Random random = new Random();
        long randomLong = min + (long)(random.nextDouble() * (max - min + 1));

        // 取大于等于该id的第一条数据
        DailyWords dailyWords = this.lambdaQuery().ge(DailyWords::getId, randomLong).last("limit 1").one();
        return DailyWordsDto.transferDto(dailyWords);
    }
}




