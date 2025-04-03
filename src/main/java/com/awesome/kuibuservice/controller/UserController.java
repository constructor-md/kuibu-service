package com.awesome.kuibuservice.controller;


import com.awesome.kuibuservice.annotation.Login;
import com.awesome.kuibuservice.aop.UserInfoContext;
import com.awesome.kuibuservice.commons.R;
import com.awesome.kuibuservice.exception.ErrorCode;
import com.awesome.kuibuservice.exception.ThrowUtils;
import com.awesome.kuibuservice.model.dto.UserInfoDto;
import com.awesome.kuibuservice.model.dto.UserSuggestionDto;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.awesome.kuibuservice.model.entity.UserSuggestion;
import com.awesome.kuibuservice.service.UserInfoService;
import com.awesome.kuibuservice.service.UserSuggestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RequestMapping("/user")
@Slf4j
@RestController
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserSuggestionService userSuggestionService;


    /**
     * 微信登陆
     */
    @PostMapping("/login")
    public R<String> getUserInfo(@RequestParam("code") String code) {
        return R.ok(userInfoService.login(code));
    }

    /**
     * 修改用户名
     */
    @Login
    @PutMapping("/username")
    public R<?> updateUsername(@RequestParam("username") String username) {
        UserInfo userInfo = UserInfoContext.get();
        userInfo.setUsername(username);
        userInfo.setUpdateTime(new Date());
        boolean result = userInfoService.updateById(userInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATOR_ERROR, "更新username信息失败");
        return R.ok();
    }

    /**
     * 修改头像
     */
    @Login
    @PutMapping("/avatar")
    public R<?> updateAvatar(@RequestParam("avatar") String avatarUrl) {
        UserInfo userInfo = UserInfoContext.get();
        userInfo.setAvatar(avatarUrl);
        userInfo.setUpdateTime(new Date());
        boolean result = userInfoService.updateById(userInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATOR_ERROR, "更新avatar信息失败");
        return R.ok();
    }

    /**
     * 获取用户信息
     */
    @Login
    @GetMapping("/userinfo")
    public R<UserInfoDto> getUserInfo() {
        UserInfo userInfo = UserInfoContext.get();
        return R.ok(UserInfoDto.transferDto(userInfo));
    }

    /**
     * 提交建议
     */
    @Login
    @PostMapping("/suggestion")
    public R<?> userSuggestion(@RequestBody UserSuggestionDto userSuggestionDto) {
        UserInfo userInfo = UserInfoContext.get();
        UserSuggestion userSuggestion = new UserSuggestion();
        userSuggestion.setText(userSuggestionDto.getText());
        userSuggestion.setUserId(userInfo.getId());
        userSuggestion.setCreateTime(new Date());
        boolean result = userSuggestionService.save(userSuggestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATOR_ERROR, "保存用户建议信息失败");
        return R.ok();
    }

}
