package com.awesome.kuibuservice.controller;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.awesome.kuibuservice.annotation.Login;
import com.awesome.kuibuservice.aop.UserInfoContext;
import com.awesome.kuibuservice.commons.R;
import com.awesome.kuibuservice.exception.ErrorCode;
import com.awesome.kuibuservice.exception.ThrowUtils;
import com.awesome.kuibuservice.model.PageRequest;
import com.awesome.kuibuservice.model.dto.GoalInfoDto;
import com.awesome.kuibuservice.model.entity.GoalInfo;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.awesome.kuibuservice.model.enums.GoalAndTaskStatusEnum;
import com.awesome.kuibuservice.service.GoalInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/goal")
public class GoalController {

    @Resource
    private GoalInfoService goalInfoService;

    /**
     * 查询目标列表
     */
    @Login
    @GetMapping("/list")
    public R<List<GoalInfoDto>> getGoalInfoList() {
        UserInfo userInfo = UserInfoContext.get();
        List<GoalInfo> goalInfoList = goalInfoService.lambdaQuery().eq(GoalInfo::getUserId, userInfo.getId()).orderByDesc(GoalInfo::getCreateTime).list();
        return R.ok(goalInfoList.stream().map(GoalInfoDto::transferDto).collect(Collectors.toList()));
    }

    /**
     * 查询目标分页
     */
    @Login
    @PostMapping("/page")
    public R<Page<GoalInfoDto>> getGoalInfoPage(@RequestBody PageRequest pageRequest) {
        UserInfo userInfo = UserInfoContext.get();
        QueryWrapper<GoalInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotEmpty(userInfo.getId()), "user_id", userInfo.getId());
        queryWrapper.orderBy(true, false, "create_time");
        Page<GoalInfo> goalInfoPage = goalInfoService.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), queryWrapper);
        Page<GoalInfoDto>  goalInfoPageDto = new Page<>(goalInfoPage.getCurrent(), goalInfoPage.getSize(), goalInfoPage.getTotal());;
        goalInfoPageDto.setRecords(goalInfoPage.getRecords().stream().map(GoalInfoDto::transferDto).collect(Collectors.toList()));
        return R.ok(goalInfoPageDto);
    }

    /**
     * 新建目标
     */
    @Login
    @PostMapping("/info")
    public R<?> addGoal(@RequestParam("name") String name) {
        UserInfo userInfo = UserInfoContext.get();
        GoalInfo goalInfo = new GoalInfo();
        goalInfo.setName(name);
        goalInfo.setStatus(GoalAndTaskStatusEnum.ONGOING.getStatus());
        goalInfo.setUserId(userInfo.getId());
        goalInfo.setCreateTime(new Date());
        goalInfo.setUpdateTime(new Date());
        goalInfoService.save(goalInfo);
        return R.ok();
    }

    /**
     * 修改目标
     */
    @Login
    @PutMapping("/info")
    public R<?> updateGoal(@RequestBody GoalInfoDto goalInfoDto) {
        UserInfo userInfo = UserInfoContext.get();
        boolean result = goalInfoService.lambdaUpdate()
                .eq(GoalInfo::getId, Long.valueOf(goalInfoDto.getId()))
                .eq(GoalInfo::getUserId, userInfo.getId())
                .set(GoalInfo::getName, goalInfoDto.getName())
                .set(GoalInfo::getUpdateTime, new Date())
                .update();
        ThrowUtils.throwIf(!result, ErrorCode.OPERATOR_ERROR, "更新goalName信息失败");
        return R.ok();
    }

    /**
     * 删除目标
     */
    @Login
    @DeleteMapping("/info")
    public R<?> deleteGoal(@RequestParam("id") String goalId) {
        UserInfo userInfo = UserInfoContext.get();
        boolean result = goalInfoService.removeByGoalId(Long.valueOf(goalId), userInfo.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATOR_ERROR, "删除目标信息错误: result = " + result);
        return R.ok();
    }




    /**
     * 用户进行中的目标总数
     */
    @Login
    @GetMapping("/ongoing/count")
    public R<Long> getOngoingGoalCount() {
        UserInfo userInfo = UserInfoContext.get();
        Long count = goalInfoService.lambdaQuery()
                .eq(GoalInfo::getUserId, userInfo.getId())
                .eq(GoalInfo::getStatus, GoalAndTaskStatusEnum.ONGOING.getStatus())
                .count();
        return R.ok(count);
    }


}
