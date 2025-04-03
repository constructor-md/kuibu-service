package com.awesome.kuibuservice.controller;

import com.awesome.kuibuservice.annotation.Login;
import com.awesome.kuibuservice.annotation.RequestLimited;
import com.awesome.kuibuservice.aop.UserInfoContext;
import com.awesome.kuibuservice.commons.Constants;
import com.awesome.kuibuservice.commons.R;
import com.awesome.kuibuservice.model.dto.ai.AnalysisDto;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.awesome.kuibuservice.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/ai")
public class AiController {

    @Resource
    private AiService aiService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 获取建议
     * 只从缓存获取，进入页面时调用
     * 初始数据来自夜间执行定时任务写入，或者上次生成
     */
    @Login
    @GetMapping("/suggestion")
    public R<String> suggestion() {
        UserInfo userInfo = UserInfoContext.get();
        String content = (String) redisTemplate.opsForValue().get(Constants.REDIS_AI_SUGGESTION_PREFIX + userInfo.getId());
        return R.ok(content);
    }


    /**
     * 获取建议
     * 从接口获取流式数据，点击刷新时调用
     * 获取的流式数据最终写入缓存
     */
    @Login
    @RequestLimited
    @GetMapping(path = "/suggestion/refresh", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamData() {
        return aiService.getStreamSuggestionByV3();
    }

    @Login
    @GetMapping("/test")
    public R<AnalysisDto> getAna() {
        return R.ok(aiService.getAnalysisDto());
    }

}
