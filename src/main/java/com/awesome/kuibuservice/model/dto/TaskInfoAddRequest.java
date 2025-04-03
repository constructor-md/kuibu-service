package com.awesome.kuibuservice.model.dto;

import lombok.Data;

@Data
public class TaskInfoAddRequest {

    private String name;
    private Integer type;
    private Long amount;
    private String goalId;

}
