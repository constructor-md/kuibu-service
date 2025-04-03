package com.awesome.kuibuservice.model.dto;

import com.awesome.kuibuservice.model.entity.DailyWords;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class DailyWordsDto {

    private String sentence;
    private String author;
    private String source;

    public static DailyWordsDto transferDto(DailyWords dailyWords) {
        DailyWordsDto dailyWordsDto = new DailyWordsDto();
        BeanUtils.copyProperties(dailyWords, dailyWordsDto);
        return dailyWordsDto;
    }

    public static DailyWords transferDB(DailyWordsDto dailyWordsDto) {
        DailyWords dailyWords = new DailyWords();
        BeanUtils.copyProperties(dailyWordsDto, dailyWords);
        dailyWords.setCreateTime(new Date());
        return dailyWords;
    }
}
