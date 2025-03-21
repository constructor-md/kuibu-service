package com.awesome.kuibuservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.awesome.kuibuservice.commons.Constants;
import com.awesome.kuibuservice.exception.BusinessException;
import com.awesome.kuibuservice.exception.ErrorCode;
import com.awesome.kuibuservice.exception.ThrowUtils;
import com.awesome.kuibuservice.model.dto.WechatSessionResponse;
import com.awesome.kuibuservice.model.entity.SysConfig;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.awesome.kuibuservice.service.SysConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.awesome.kuibuservice.service.UserInfoService;
import com.awesome.kuibuservice.mapper.UserInfoMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 82611
 * @description 针对表【user_info】的数据库操作Service实现
 * @createDate 2025-03-20 13:33:38
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
        implements UserInfoService {


    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SysConfigService sysConfigService;


    @Override
    @Transactional
    public String login(String code) {

        String wxLoginUrl = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.WX_LOGIN_URL).one().getSysValue();
        String wxAppID = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.WX_APP_ID).one().getSysValue();
        String wxAppSecret = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.WX_APP_SECRET).one().getSysValue();

        String url = String.format(wxLoginUrl, wxAppID, wxAppSecret, code);

        WechatSessionResponse session = null;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    url, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信登录失败：接口调用异常");
            }

            session = JSONUtil.parseObj(response.getBody()).toBean(WechatSessionResponse.class);
            if (session.getErrcode() != null && session.getErrcode() != 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信登录失败：接口调用异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信登录失败：接口调用异常");
        }

        // 保存用户信息
        String openId = session.getOpenid();
        UserInfo userInfo = this.lambdaQuery().eq(UserInfo::getOpenId, openId).one();
        if (userInfo != null) {
            // 更新
            boolean result = this.lambdaUpdate().eq(UserInfo::getOpenId, openId)
                    .set(UserInfo::getSessionKey, session.getSession_key())
                    .set(UserInfo::getUpdateTime, new Date()).update();
            ThrowUtils.throwIf(!result, ErrorCode.OPERATOR_ERROR, "更新session_key信息失败");
        } else {
            // 新增
            userInfo = new UserInfo();
            userInfo.setOpenId(openId);
            userInfo.setUsername("无名");
            userInfo.setSessionKey(session.getSession_key());
            userInfo.setAvatar(null);
            userInfo.setCreateTime(new Date());
            userInfo.setUpdateTime(new Date());
            boolean result = this.save(userInfo);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATOR_ERROR, "保存用户信息失败");
        }

        // 生成Token并存储Redis value为userId
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(Constants.REDIS_ACCESS_TOKEN_PREFIX + token, userInfo.getId(), 1, TimeUnit.DAYS);

        return token;
    }
}




