package com.awesome.kuibuservice.components;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.awesome.kuibuservice.model.dto.DailyWordsDto;
import com.awesome.kuibuservice.model.dto.ai.SiliconFlowRequest;
import com.awesome.kuibuservice.model.entity.DailyWords;
import com.awesome.kuibuservice.service.AiService;
import com.awesome.kuibuservice.service.DailyWordsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SentenceComponent implements CommandLineRunner {

    @Resource
    private AiService aiService;

    @Resource
    private DailyWordsService dailyWordsService;

    // 句子存放阻塞队列
    private static final LinkedBlockingQueue<String> sentenceBlockingQueue = new LinkedBlockingQueue<>();

    // 消费线程池
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private static final String SYSTEM_PROMPT = "" +
            "将我传递给你的文本，其中的励志含义句子解析成JSON数组，不需要返回任何其他内容，整体完全符合JSON规范。" +
            "数组内每个对象包含属性sentence author source，分别代表句子内容、作者和来源书目。" +
            "优先从文本中取得其作者和来源书目，如果没有，需要你查询获取。" +
            "那些作者和来源书目，为空的、未知的、和佚名的结果，直接不返回。" +
            "不需要返回句子长度小于15，大于35的结果。";


    private SiliconFlowRequest getSiliconFlowRequest() {
        return new SiliconFlowRequest()
                .setModel("deepseek-ai/DeepSeek-V3")
                .setStream(false)
                .setTemperature(0.0)
                .setTopP(1.0)
                .setN(1)
                .setMaxTokens(2048)
                .setResponseFormat(new SiliconFlowRequest.ResponseFormat().setType("text"));
    }

    private static final SiliconFlowRequest.Message systemMessage = new SiliconFlowRequest.Message()
            .setRole("system")
            .setContent(SYSTEM_PROMPT);

    // 相似度阈值
    private static final double SIMILARITY_THRESHOLD = 0.4;

    public void save(String sentence) {
        try {
            sentenceBlockingQueue.put(sentence);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        // 执行完毕后间隔五秒再执行一次
        scheduledExecutorService.scheduleWithFixedDelay(this::task, 0, 5, TimeUnit.SECONDS);

    }

    public void task() {
        try {
            String content = sentenceBlockingQueue.take();
            // 调用AI解析转换过滤
            List<SiliconFlowRequest.Message> messages = new ArrayList<>();
            SiliconFlowRequest.Message message = new SiliconFlowRequest.Message()
                    .setRole("user")
                    .setContent(content);
            messages.add(systemMessage);
            messages.add(message);
            SiliconFlowRequest request = getSiliconFlowRequest();
            request.setMessages(messages);
            List<DailyWordsDto> aiResultList;
            try {
                String aiResult = aiService.aiProcess(request);
                aiResultList = JSONUtil.parseArray(aiResult).toList(DailyWordsDto.class);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (CollectionUtil.isEmpty(aiResultList)) {
                log.info("aiResult is empty");
                return;
            }
            // 内容过滤
            aiResultList = aiResultList.stream().filter(r ->
                            !(
                                    StrUtil.isBlank(r.getSource()) || StrUtil.isBlank(r.getAuthor())
                                            || r.getAuthor().equals("未知") || r.getAuthor().equals("佚名")
                                            || r.getSource().equals("未知") || r.getSource().equals("佚名")
                                    )
            ).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(aiResultList)) {
                return;
            }
            // 句子和完整对象映射
            Map<String, DailyWordsDto> sentence2All = aiResultList.stream()
                    .collect(Collectors.toMap(DailyWordsDto::getSentence, d -> d, (old, now) -> now));
            // 句子和词频向量映射
            Map<String, Map<String, Integer>> sentence2Freq = aiResultList.stream()
                    .collect(Collectors.toMap(DailyWordsDto::getSentence, d -> getFrequencyMap(d.getSentence()), (old, now) -> now));
            // 查询数据库数据并做相似度计算和排除
            long pageSize = 1000;
            int currentPage = 1;
            // 每个句子存储，都要遍历每个记录计算 成本很高！
            // 每个句子都查一遍数据库绝对不行，应该只一遍数据库，只要有不符合条件的数据库数据，就能从sentence2All中剔除避免写入
            while (true) {
                // 句子为空后不用继续
                if (CollectionUtil.isEmpty(sentence2All)) {
                    return;
                }
                Page<DailyWords> page = new Page<>(currentPage, pageSize);
                Page<DailyWords> resultPage = dailyWordsService.page(page, new QueryWrapper<>());
                currentPage++;
                if (resultPage.getRecords().isEmpty()) {
                    break; // 如果当前页没有数据，结束循环
                }
                List<DailyWords> sqlResult = resultPage.getRecords();
                // 计算每个句子的词频向量
                List<Map<String, Integer>> freqList = sqlResult.stream()
                        .map(item -> getFrequencyMap(item.getSentence()))
                        .collect(Collectors.toList());

                // 不符合相似条件的句子 应该被剔除
                List<String> remove = new ArrayList<>();
                sentence2Freq.forEach((key, value) -> {
                    freqList.forEach(freq -> {
                        double cal = calculate(value, freq);
                        if (cal > SIMILARITY_THRESHOLD) {
                            remove.add(key);
                        }
                    });
                });

                // 剔除句子，减轻后续循环的次数
                remove.forEach(r -> {
                    sentence2All.remove(r);
                    sentence2Freq.remove(r);
                });

            }

            // 保存剩余的句子
            if (!CollectionUtil.isEmpty(sentence2All)) {
                List<DailyWords> dailyWordsList = sentence2All.values().stream().map(DailyWordsDto::transferDB).collect(Collectors.toList());
                dailyWordsService.saveBatch(dailyWordsList);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Map<String, Integer> getFrequencyMap(String sentence) {
        List<Term> termList = HanLP.segment(sentence);
        Map<String, Integer> freqMap = new HashMap<>();
        for (Term term : termList) {
            String word = term.word;
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }
        return freqMap;
    }

    private double calculate(Map<String, Integer> freq1, Map<String, Integer> freq2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        Set<String> allWords = new HashSet<>(freq1.keySet());
        allWords.addAll(freq2.keySet());

        for (String word : allWords) {
            int freq1Value = freq1.getOrDefault(word, 0);
            int freq2Value = freq2.getOrDefault(word, 0);
            dotProduct += freq1Value * freq2Value;
            norm1 += Math.pow(freq1Value, 2);
            norm2 += Math.pow(freq2Value, 2);
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // 启动执行
    @Override
    public void run(String... args) throws Exception {
        start();
    }
}
