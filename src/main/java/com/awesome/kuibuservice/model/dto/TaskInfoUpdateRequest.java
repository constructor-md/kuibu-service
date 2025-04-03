package com.awesome.kuibuservice.model.dto;

import lombok.Data;

@Data
public class TaskInfoUpdateRequest {

    private Long id;
    private String name;
    private Long amount;
    private String goalId;

}
