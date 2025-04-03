package com.awesome.kuibuservice.model.dto;

import lombok.Data;

@Data
public class KuibuInfoRequest {


    private Integer type;
    private String taskId;
    private Long count;

}
