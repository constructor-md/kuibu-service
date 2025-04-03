package com.awesome.kuibuservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 请求频率限制
 * 默认十分钟请求一次
 * 由接口实现每用户每十分钟执行一次
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimited {

    /**
     * 限制时间
     */
    long limit() default 10L;

    /**
     * 限制单位
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

}
