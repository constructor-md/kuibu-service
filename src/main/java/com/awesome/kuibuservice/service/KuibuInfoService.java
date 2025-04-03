package com.awesome.kuibuservice.service;

import com.awesome.kuibuservice.model.dto.BarchartDto;
import com.awesome.kuibuservice.model.dto.KuibuInfoRequest;
import com.awesome.kuibuservice.model.entity.KuibuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 82611
* @description 针对表【kuibu_info】的数据库操作Service
* @createDate 2025-03-20 13:34:47
*/
public interface KuibuInfoService extends IService<KuibuInfo> {

    void addRecord(KuibuInfoRequest kuibuInfoRequest);

    BarchartDto getTaskBarChart(Long taskId);

    void removeByTaskIdList(List<Long> taskIds, Long userId);

}
