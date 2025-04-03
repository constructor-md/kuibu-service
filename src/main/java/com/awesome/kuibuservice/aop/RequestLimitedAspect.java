package com.awesome.kuibuservice.aop;

import com.awesome.kuibuservice.annotation.RequestLimited;
import com.awesome.kuibuservice.commons.Constants;
import com.awesome.kuibuservice.exception.ErrorCode;
import com.awesome.kuibuservice.exception.ThrowUtils;
import com.awesome.kuibuservice.filter.RequestWrapper;
import com.awesome.kuibuservice.model.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@Order(value = 3)
public class RequestLimitedAspect {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Before("@annotation(requestLimited)")
    public void requestLimited(RequestLimited requestLimited) {
        UserInfo userInfo = UserInfoContext.get();
        RequestAttributes requestAttribute = RequestContextHolder.currentRequestAttributes();
        RequestWrapper request = (RequestWrapper) ((ServletRequestAttributes) requestAttribute).getRequest();
        String url = request.getRequestURI();
        long time = requestLimited.limit();
        TimeUnit timeUnit = requestLimited.timeUnit();
        String key = Constants.REDIS_REQUEST_LIMITED_PREFIX + url + "::" + userInfo.getId();
        // 判断是否存在Key
        Boolean exist = redisTemplate.hasKey(key);
        ThrowUtils.throwIf(exist != null && exist, ErrorCode.REQUEST_LIMITED);
        // 每用户每指定时间内可调用一次
        redisTemplate.opsForValue().set(key, "", time, timeUnit);
    }

}
