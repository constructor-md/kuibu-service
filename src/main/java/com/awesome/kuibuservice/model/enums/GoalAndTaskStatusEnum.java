package com.awesome.kuibuservice.model.enums;

import lombok.Getter;

@Getter
public enum GoalAndTaskStatusEnum {

    ONGOING(0, "进行中"),
    COMPLETE(1, "已完成")
    ;

    private final int status;
    private final String desc;

    GoalAndTaskStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

}
