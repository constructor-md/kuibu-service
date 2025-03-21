package com.awesome.kuibuservice.aop;

import cn.hutool.core.util.StrUtil;
import com.awesome.kuibuservice.commons.Constants;
import com.awesome.kuibuservice.exception.BusinessException;
import com.awesome.kuibuservice.exception.ErrorCode;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.awesome.kuibuservice.service.UserInfoService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Order(value = 1)
public class LoginAspect {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserInfoService userInfoService;

    @Pointcut(value = "@annotation(com.awesome.kuibuservice.annotation.Login))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object checkToken(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            RequestAttributes requestAttribute = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttribute).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isBlank(token)) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            // 检查有效
            String userId = (String) redisTemplate.opsForValue().get(Constants.REDIS_ACCESS_TOKEN_PREFIX + token);
            if (StrUtil.isBlank(userId)) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            } else {
                // 保存用户信息上下文以便使用
                UserInfo userInfo = userInfoService.getById(userId);
                UserInfoContext.set(userInfo);
            }
            return joinPoint.proceed();
        } finally {
            // 清理上下文 避免内存泄漏
            UserInfoContext.clear();
        }
    }


}
