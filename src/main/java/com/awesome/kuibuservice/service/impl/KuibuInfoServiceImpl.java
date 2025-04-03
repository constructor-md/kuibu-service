package com.awesome.kuibuservice.service.impl;

import com.awesome.kuibuservice.aop.UserInfoContext;
import com.awesome.kuibuservice.exception.ErrorCode;
import com.awesome.kuibuservice.exception.ThrowUtils;
import com.awesome.kuibuservice.mapper.TaskInfoMapper;
import com.awesome.kuibuservice.model.dto.BarchartDto;
import com.awesome.kuibuservice.model.dto.TaskDateGroupDto;
import com.awesome.kuibuservice.model.dto.KuibuInfoRequest;
import com.awesome.kuibuservice.model.entity.KuibuInfo;
import com.awesome.kuibuservice.model.entity.TaskInfo;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.awesome.kuibuservice.service.KuibuInfoService;
import com.awesome.kuibuservice.mapper.KuibuInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 82611
* @description 针对表【kuibu_info】的数据库操作Service实现
* @createDate 2025-03-20 13:34:47
*/
@Service
public class KuibuInfoServiceImpl extends ServiceImpl<KuibuInfoMapper, KuibuInfo>
    implements KuibuInfoService{

    @Resource
    private TaskInfoMapper taskInfoMapper;

    @Override
    @Transactional
    public void addRecord(KuibuInfoRequest kuibuInfoRequest) {
        UserInfo userInfo = UserInfoContext.get();
        KuibuInfo kuibuInfo = new KuibuInfo();
        kuibuInfo.setUserId(userInfo.getId());
        kuibuInfo.setTaskId(Long.valueOf(kuibuInfoRequest.getTaskId()));
        kuibuInfo.setCount(kuibuInfoRequest.getCount());
        kuibuInfo.setCreateTime(new Date());
        kuibuInfo.setUpdateTime(new Date());

        int result1 = this.baseMapper.insert(kuibuInfo);
        ThrowUtils.throwIf(result1 <= 0, ErrorCode.OPERATOR_ERROR, "跬步新增失败");

        // 更新任务进度 原子更新 交给MVCC并发控制
        UpdateWrapper<TaskInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", kuibuInfoRequest.getTaskId());
        updateWrapper.setSql("current_progress = current_progress + " + kuibuInfoRequest.getCount());
        updateWrapper.setSql("status = CASE WHEN current_progress + " + kuibuInfoRequest.getCount() + " >= target_amount THEN 1 ELSE status END");
        int result2 =  taskInfoMapper.update(null, updateWrapper);
        ThrowUtils.throwIf(result2 <= 0, ErrorCode.OPERATOR_ERROR, "任务信息更新失败");
    }


    @Override
    public BarchartDto getTaskBarChart(Long taskId) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        int type = taskInfo.getType();
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<String> dateStrings = new ArrayList<>();
        for (LocalDate date = sevenDaysAgo; !date.isAfter(today); date = date.plusDays(1)) {
            dateStrings.add(date.format(formatter));
        }
        List<TaskDateGroupDto> taskDateGroupDtoList = this.baseMapper.selectDataGroupByDate(sevenDaysAgo.toString(), Collections.singletonList(taskId));
        Map<String, Long> date2Value = taskDateGroupDtoList.stream().collect(Collectors.toMap(TaskDateGroupDto::getDate, TaskDateGroupDto::getValue, (old, now) -> now));
        BarchartDto barchartDto = new BarchartDto();
        dateStrings.forEach(date -> {
            barchartDto.getItems().add(date);
            long value = date2Value.getOrDefault(date, 0L);
            // 如果是时间类型任务，将毫秒数转换为秒
            if (type == 1) {
                value = value / 1000;
            }
            barchartDto.getValues().add(value);
        });
        return barchartDto;
    }

    @Override
    public void removeByTaskIdList(List<Long> taskIds, Long userId) {
        this.baseMapper.delete(Wrappers.<KuibuInfo>lambdaQuery().in(KuibuInfo::getTaskId, taskIds).eq(KuibuInfo::getUserId, userId));
    }
}




