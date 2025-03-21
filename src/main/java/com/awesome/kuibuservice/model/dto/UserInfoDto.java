package com.awesome.kuibuservice.model.dto;

import com.awesome.kuibuservice.model.entity.UserInfo;
import lombok.Data;

@Data
public class UserInfoDto {

    private String username;
    private String avatar;


    public static UserInfoDto transferDto(UserInfo userInfo) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUsername(userInfo.getUsername());
        userInfoDto.setAvatar(userInfo.getAvatar());
        return userInfoDto;
    }


}
