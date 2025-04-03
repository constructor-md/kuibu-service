package com.awesome.kuibuservice.model.dto.ai;

import com.awesome.kuibuservice.model.dto.BarchartDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AnalysisDto {

    private String username;
    private List<GoalStatistic> goalStatisticList;

    @Data
    @Accessors(chain = true)
    public static class GoalStatistic {
        private String goalName;
        private List<TaskStatistic> taskStatistics;
    }

    @Data
    @Accessors(chain = true)
    public static class TaskStatistic {
        private String taskName;
        private Integer taskType;
        private BarchartDto barchartDto;
        private Double progress;
    }

}
