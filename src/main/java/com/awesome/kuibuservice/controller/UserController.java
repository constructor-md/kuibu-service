package com.awesome.kuibuservice.controller;


import com.awesome.kuibuservice.annotation.Login;
import com.awesome.kuibuservice.aop.UserInfoContext;
import com.awesome.kuibuservice.commons.R;
import com.awesome.kuibuservice.model.dto.UserInfoDto;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.awesome.kuibuservice.service.UserInfoService;
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


    @PostMapping("/login")
    public R<String> getUserInfo(@RequestParam("code") String code) {
        return R.ok(userInfoService.login(code));
    }

    @Login
    @PutMapping("/username")
    private R<?> updateUsername(@RequestParam("username") String username) {
        UserInfo userInfo = UserInfoContext.get();
        userInfo.setUsername(username);
        userInfo.setUpdateTime(new Date());
        userInfoService.updateById(userInfo);
        return R.ok();
    }

    @Login
    @PutMapping("/avatar")
    private R<?> updateAvatar(@RequestParam("avatar") String avatarUrl) {
        UserInfo userInfo = UserInfoContext.get();
        userInfo.setAvatar(avatarUrl);
        userInfo.setUpdateTime(new Date());
        userInfoService.updateById(userInfo);
        return R.ok();
    }

    @Login
    @GetMapping("/userinfo")
    private R<UserInfoDto> getUserInfo() {
        UserInfo userInfo = UserInfoContext.get();
        return R.ok(UserInfoDto.transferDto(userInfo));
    }

}
