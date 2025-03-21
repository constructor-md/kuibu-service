package com.awesome.kuibuservice.model.dto;

import lombok.Data;

@Data
public class WechatSessionResponse {
    private String openid;
    private String session_key;
    private String unionid;
    private Integer errcode;
    private String errmsg;
}