package com.awesome.kuibuservice.aop;

import cn.hutool.json.JSONUtil;
import com.awesome.kuibuservice.commons.R;
import com.awesome.kuibuservice.filter.RequestWrapper;
import com.awesome.kuibuservice.model.entity.RequestLog;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.awesome.kuibuservice.service.RequestLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@Order(value = 2)
public class RequestLogAspect {

    @Resource
    private RequestLogService requestLogService;

    @Pointcut(value = "execution(* com.awesome.kuibuservice.controller.*.*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttribute = RequestContextHolder.currentRequestAttributes();
        RequestWrapper request = (RequestWrapper) ((ServletRequestAttributes) requestAttribute).getRequest();
        RequestLog requestLog = new RequestLog();
        requestLog.setUrl(request.getRequestURI());
        requestLog.setMethod(request.getMethod());
        Map<String, String> headers = getHeadersInfo(request);
        requestLog.setHeaders(JSONUtil.toJsonStr(headers));
        requestLog.setBody(request.getBody());
        requestLog.setClientIp(getIpAddr(request));
        requestLog.setUserAgent(headers.get(HttpHeaders.USER_AGENT.toLowerCase()));
        long startTime = System.currentTimeMillis();
        requestLog.setRequestTime(new Date(startTime));
        UserInfo userInfo = UserInfoContext.get();
        if (userInfo != null) {
            requestLog.setUserId(userInfo.getId());
        }
        Object data = null;
        try {
            data = joinPoint.proceed();
            return data;
        } finally {
            try {
                long endTime = System.currentTimeMillis();
                requestLog.setResponseTime(new Date(endTime));
                requestLog.setDuration(endTime - startTime);
                R<?> result = JSONUtil.parseObj(data).toBean(R.class);
                // 0成功 1失败
                int status = result.getCode() == 200 ? 0 : 1;
                requestLog.setStatus(status);
                if (status == 1) {
                    requestLog.setErrorCode(result.getCode());
                    requestLog.setErrorMessage(result.getMsg());
                }
                requestLog.setCreateTime(new Date());
                requestLog.setUpdateTime(new Date());
                requestLogService.save(requestLog);
            } catch (Exception e) {
                log.error("保存请求日志失败", e);
            }

        }

    }

    public Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement().toLowerCase();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }


    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IP = "127.0.0.1";
    // 客户端与服务器同为一台机器，获取的 ip 有时候是 ipv6 格式
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String SEPARATOR = ",";

    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (LOCALHOST_IP.equalsIgnoreCase(ip) || LOCALHOST_IPV6.equalsIgnoreCase(ip)) {
                // 根据网卡取本机配置的 IP
                InetAddress iNet = null;
                try {
                    iNet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (iNet != null)
                    ip = iNet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，分割出第一个 IP
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(SEPARATOR) > 0) {
                ip = ip.substring(0, ip.indexOf(SEPARATOR));
            }
        }
        return LOCALHOST_IPV6.equals(ip) ? LOCALHOST_IP : ip;
    }

}
