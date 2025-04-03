package com.awesome.kuibuservice.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class BarchartDto {

    private List<String> items = new ArrayList<>();
    private List<Number> values = new ArrayList<>();



}
