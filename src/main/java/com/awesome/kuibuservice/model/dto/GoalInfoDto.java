package com.awesome.kuibuservice.model.dto;

import com.awesome.kuibuservice.model.entity.GoalInfo;
import lombok.Data;

@Data
public class GoalInfoDto {

    private String id;
    private String name;

    public static GoalInfoDto transferDto(GoalInfo goalInfo) {
        GoalInfoDto goalInfoDto = new GoalInfoDto();
        goalInfoDto.setId(String.valueOf(goalInfo.getId()));
        goalInfoDto.setName(goalInfo.getName());
        return goalInfoDto;
    }

}
