package com.awesome.kuibuservice.controller;

import com.awesome.kuibuservice.annotation.Login;
import com.awesome.kuibuservice.commons.R;
import com.awesome.kuibuservice.components.SentenceComponent;
import com.awesome.kuibuservice.model.dto.DailyWordsDto;
import com.awesome.kuibuservice.model.entity.DailyWords;
import com.awesome.kuibuservice.service.AiService;
import com.awesome.kuibuservice.service.DailyWordsService;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sentence")
public class DailyWordsController {

    @Resource
    private DailyWordsService dailyWordsService;

    @Resource
    private AiService aiService;

    @Resource
    private SentenceComponent sentenceComponent;

    /**
     * 获取每日一句
     *
     */
    @Login
    @GetMapping("/one")
    public R<DailyWordsDto> getOneDailyWords() {
        return R.ok(dailyWordsService.getRandomOneSentence());
    }

    /**
     * 批量存储每日一句
     * 自动剔除重复句子
     */
    @Login
    @PostMapping("/list")
    public R<?> addDailyWords(@RequestBody String sentence) {
        sentenceComponent.save(sentence);
        sentenceComponent.task();
        return R.ok();
    }


}
