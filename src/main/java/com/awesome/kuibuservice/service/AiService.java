package com.awesome.kuibuservice.service;

import com.awesome.kuibuservice.model.dto.ai.AnalysisDto;
import com.awesome.kuibuservice.model.dto.ai.SiliconFlowRequest;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface AiService {

    String aiProcess(SiliconFlowRequest request);

    Flux<String> aiStreamProcess(SiliconFlowRequest request);

    Flux<ServerSentEvent<String>> getStreamSuggestionByV3();

    AnalysisDto getAnalysisDto();

}
