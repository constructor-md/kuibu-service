package com.awesome.kuibuservice.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.awesome.kuibuservice.aop.UserInfoContext;
import com.awesome.kuibuservice.commons.Constants;
import com.awesome.kuibuservice.commons.R;
import com.awesome.kuibuservice.exception.BusinessException;
import com.awesome.kuibuservice.exception.ErrorCode;
import com.awesome.kuibuservice.mapper.KuibuInfoMapper;
import com.awesome.kuibuservice.model.dto.BarchartDto;
import com.awesome.kuibuservice.model.dto.TaskDateGroupDto;
import com.awesome.kuibuservice.model.dto.ai.AnalysisDto;
import com.awesome.kuibuservice.model.dto.ai.ChatCompletionChunk;
import com.awesome.kuibuservice.model.dto.ai.SiliconFlowRequest;
import com.awesome.kuibuservice.model.dto.ai.SiliconFlowResponse;
import com.awesome.kuibuservice.model.entity.GoalInfo;
import com.awesome.kuibuservice.model.entity.SysConfig;
import com.awesome.kuibuservice.model.entity.TaskInfo;
import com.awesome.kuibuservice.model.entity.UserInfo;
import com.awesome.kuibuservice.service.AiService;
import com.awesome.kuibuservice.service.GoalInfoService;
import com.awesome.kuibuservice.service.SysConfigService;
import com.awesome.kuibuservice.service.TaskInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiServiceImpl implements AiService {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private GoalInfoService goalInfoService;

    @Resource
    private TaskInfoService taskInfoService;

    @Resource
    private KuibuInfoMapper kuibuInfoMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final WebClient webClient = WebClient.create("");

    private static final String AI_SUGGESTION_SYSTEM_PROMPT = "" +
            "我会给你一个健康积极向上的用户最近设立的一些目标和任务的完成情况，请你给出简短的建议。" +
            "我提交的数据为JSON格式，其示例格式如下：" +
            "{\n" +
            "    \"username\": \"用户名\",\n" +
            "    \"goalStatisticList\": [\n" +
            "        {\n" +
            "            \"goalName\": \"目标1\",\n" +
            "            \"taskStatistics\": [\n" +
            "                {\n" +
            "                    \"taskName\": \"任务1\",\n" +
            "                    \"taskType\": 1,\n" +
            "                    \"barchartDto\": [\n" +
            "                        {\n" +
            "                            \"items\": [\n" +
            "                                \"03-02\",\n" +
            "                                \"03-03\",\n" +
            "                                \"03-04\",\n" +
            "                                \"03-05\",\n" +
            "                                \"03-06\"\n" +
            "                            ],\n" +
            "                            \"values\": [\n" +
            "                                12,\n" +
            "                                54,\n" +
            "                                23,\n" +
            "                                43,\n" +
            "                                54\n" +
            "                            ]\n" +
            "                        }\n" +
            "                    ],\n" +
            "                    \"progress\": 88.3\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}" +
            "我每次提交一名用户的多个目标goal统计情况在goalStatisticList，每个目标goal有多个任务统计信息在taskStatistics，" +
            "每个任务统计信息中barchartDto表示用户最近一周七天内的任务完成情况，items是日期，values是对应每天的完成量" +
            "当taskType=1时，values中的数据应该理解为时间的秒数;taskType=2时，values中的数据应该理解为执行任务的次数；" +
            "progress代表该任务目前的执行进度的百分比。" +
            "我需要根据传递的信息，从中为用户提出鼓励和表扬，并指点用户接下来应该如何改进自己的计划。" +
            "你返回给我的建议是纯文本txt格式，但是为了展示的美观，需要你采用换行符等符号换行和占位。" +
            "不要质疑我给出的数据的正确错误和重复性等，你应该把数据作为实际的有效的内容进行评判。";


    private SiliconFlowRequest getAISuggestionStreamRequest() {
        List<SiliconFlowRequest.Message> messages = new ArrayList<>();
        SiliconFlowRequest.Message message =
                new SiliconFlowRequest.Message()
                        .setRole("system")
                        .setContent(AI_SUGGESTION_SYSTEM_PROMPT);
        messages.add(message);
        return new SiliconFlowRequest()
                .setModel("deepseek-ai/DeepSeek-V3")
                .setStream(true)
                .setTemperature(0.8)
                .setTopP(1.0)
                .setN(1)
                .setMaxTokens(2048)
                .setMessages(messages)
                .setResponseFormat(new SiliconFlowRequest.ResponseFormat().setType("text"));
    }

    @Override
    public String aiProcess(SiliconFlowRequest request) {
        String chatUrl = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.SILICON_FLOW_CHAT_URL).one().getSysValue();
        String apiKey = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.SILICON_FLOW_API_KEY).one().getSysValue();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            // 创建请求实体
            HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<SiliconFlowResponse> response =
                    restTemplate.exchange(chatUrl, HttpMethod.POST, requestEntity, SiliconFlowResponse.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "硅基流动接口调用异常");
            }
            System.out.println(response.getBody().getChoices().get(0).getMessage().getContent());
            return response.getBody().getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "硅基流动接口调用异常");
        }
    }

    @Override
    public Flux<String> aiStreamProcess(SiliconFlowRequest request) {
        String chatUrl = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.SILICON_FLOW_CHAT_URL).one().getSysValue();
        String apiKey = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.SILICON_FLOW_API_KEY).one().getSysValue();

        return webClient.post()
                .uri(chatUrl)
                .headers(httpHeaders -> {
                    httpHeaders.set("Authorization", "Bearer " + apiKey);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToFlux(String.class);
    }

    @Override
    public Flux<ServerSentEvent<String>> getStreamSuggestionByV3() {
        UserInfo userInfo = UserInfoContext.get();
        SiliconFlowRequest request = getAISuggestionStreamRequest();
        AnalysisDto analysisDto = getAnalysisDto();
        SiliconFlowRequest.Message userMessage =
                new SiliconFlowRequest.Message()
                        .setRole("user")
                        .setContent(JSONUtil.toJsonStr(analysisDto));
        request.getMessages().add(userMessage);

        Flux<String> responseFlux = aiStreamProcess(request);
        StringBuilder fullData = new StringBuilder();
        return responseFlux
                .flatMap(chunk -> {
                    System.out.println(chunk);
                    String words = "";
                    if (!chunk.equals("[DONE]")) {
                        ChatCompletionChunk chatCompletionChunk = JSONUtil.parseObj(chunk).toBean(ChatCompletionChunk.class);
                        words = chatCompletionChunk.getChoices().get(0).getDelta().getContent();
                    }
                    fullData.append(words);
                    return Flux.just(words)
                            .map(data -> ServerSentEvent.<String>builder()
                                    .data(JSONUtil.toJsonStr(R.ok(data)))
                                    .build());
                })
                .onErrorResume(error -> {
                    log.error("AI建议刷新发生异常", error);
                    return Flux.just(ServerSentEvent.<String>builder()
                            .data("请求发生异常，请稍后重试")
                            .build());
                })
                .doOnComplete(() -> {
                    // 流式数据传输完毕后，将数据整体缓存到 Redis 中
                    redisTemplate.opsForValue().set(Constants.REDIS_AI_SUGGESTION_PREFIX + userInfo.getId(), fullData.toString());
                });
    }

    @Override
    public AnalysisDto getAnalysisDto() {
        UserInfo userInfo = UserInfoContext.get();
        AnalysisDto analysisDto = new AnalysisDto();
        List<AnalysisDto.GoalStatistic> goalStatisticList = new ArrayList<>();
        analysisDto.setUsername(userInfo.getUsername());
        analysisDto.setGoalStatisticList(goalStatisticList);
        // 获取目标列表
        List<GoalInfo> goalInfoList = goalInfoService.lambdaQuery().eq(GoalInfo::getUserId, userInfo.getId()).list();
        Map<Long, GoalInfo> goalInfoMap = goalInfoList.stream()
                .collect(Collectors.toMap(GoalInfo::getId, d -> d, (old, now) -> now));
        Set<Long> goalIdList = goalInfoMap.keySet();
        // 获取任务列表
        List<TaskInfo> taskInfoList = taskInfoService.lambdaQuery().in(TaskInfo::getGoalId, goalIdList).list();
        Set<Long> taskIdList = taskInfoList.stream().map(TaskInfo::getId).collect(Collectors.toSet());
        Map<Long, List<TaskInfo>> goal2TaskGroup = taskInfoList.stream()
                .collect(Collectors.groupingBy(TaskInfo::getGoalId));

        // 获取每个任务最近一周执行情况统计
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<String> dateStrings = new ArrayList<>();
        for (LocalDate date = sevenDaysAgo; !date.isAfter(today); date = date.plusDays(1)) {
            dateStrings.add(date.format(formatter));
        }
        List<TaskDateGroupDto> taskDateGroupDtoList = kuibuInfoMapper.selectDataGroupByDate(sevenDaysAgo.toString(), taskIdList);
        Map<Long, List<TaskDateGroupDto>> taskId2List = taskDateGroupDtoList.stream().collect(Collectors.groupingBy(TaskDateGroupDto::getTaskId));
        for (GoalInfo goalInfo :goalInfoList) {
            AnalysisDto.GoalStatistic goalStatistic = new AnalysisDto.GoalStatistic();
            goalStatisticList.add(goalStatistic);
            goalStatistic.setGoalName(goalInfo.getName());
            List<TaskInfo> taskInfos = goal2TaskGroup.getOrDefault(goalInfo.getId(), new ArrayList<>());
            List<AnalysisDto.TaskStatistic> taskStatisticList = new ArrayList<>();
            goalStatistic.setTaskStatistics(taskStatisticList);
            for (TaskInfo taskInfo: taskInfos) {
                AnalysisDto.TaskStatistic taskStatistic = new AnalysisDto.TaskStatistic();
                taskStatisticList.add(taskStatistic);
                taskStatistic.setTaskName(taskInfo.getName());
                taskStatistic.setTaskType(taskInfo.getType());
                double percent = NumberUtil.round(taskInfo.getCurrentProgress() * 100.0 / taskInfo.getTargetAmount(), 2).doubleValue();
                percent = percent > 100 ? 100 : percent;
                taskStatistic.setProgress(percent);
                int type = taskInfo.getType();
                List<TaskDateGroupDto> taskDateGroupDtoGroup = taskId2List.getOrDefault(taskInfo.getId(), new ArrayList<>());
                Map<String, Long> date2Value = taskDateGroupDtoGroup.stream().collect(Collectors.toMap(TaskDateGroupDto::getDate, TaskDateGroupDto::getValue, (old, now) -> now));
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
                taskStatistic.setBarchartDto(barchartDto);
            }
        }

        return analysisDto;
    }
}
