package com.late.donot.calculator.model.service;

import java.time.LocalDateTime;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.api.dto.Route;
import com.late.donot.api.dto.RouteStep;
import com.late.donot.api.dto.RouteStepType;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;

    public AiServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /** 작성자 : 이승준
     *  작성일 : 2026-02-19(수정)
     *  AI가 계산후 최적시간 반환
     */
    @Override
    public String recommendPushTime(Route route,
                                    int prepareTime,
                                    int bufferTime,
                                    String weather,
                                    String departureDateTime) {

        LocalDateTime arrival = LocalDateTime.parse(departureDateTime);

        String timeZone = isRushHour(arrival) ? "출퇴근 시간대" : "일반 시간대";
        String trafficType = resolveTrafficType(route);

        String prompt = """
        		당신은 '통근 최적화 전략가'입니다. 
        		단순 계산을 넘어, 제공된 상황의 위험 요소(환승, 시간대, 날씨)를 분석하여 최적의 [Push 알림 시각]을 제안하세요.

        		[사용자 상황 데이터]
        		1. 도착 희망 시각: %s (정각 도착은 위험할 수 있음)
        		2. 이동 데이터: 총 %d분 소요, 환승 %d회 (교통수단: %s)
        		3. 준비 시간: %d분
        		4. 여유 시간(Buffer): %d분
        		5. 외부 변수: %s(날씨), %s(시간대)
        		
        		[추론 가이드 - AI의 생각할 여지]
        		- 환승 횟수가 많다면, 환승 대기 및 이동 중 변수를 고려하여 여유 시간을 스스로 가중치 있게 조정하세요.
        		- %s(시간대)와 %s(날씨)가 이동 속도나 대중교통 혼잡도에 미치는 영향을 반영하세요.
        		- 사용자가 '준비를 시작해야 하는 시각'에 알림을 주어야 합니다.
        		- (계산 공식의 기본: 도착 시각 - 이동 시간 - 준비 시간 - 여유 시간)이나,
        		  당신의 분석에 따라 ±5~10분 정도의 전략적 조정을 허용합니다.

        		[최종 출력 규정]
        		- 분석 과정은 생략하고, 최종적으로 도출된 [Push 알림 시각] 딱 하나만 HH:mm 형식으로 출력하세요.
        		- 예: 07:45
        		""".formatted(
        		    arrival.toLocalTime(),
        		    route.getTotalTime(),
        		    route.getTransferCount(),
        		    trafficType,
        		    prepareTime,
        		    bufferTime,
        		    weather,
        		    timeZone,
        		    timeZone,
        		    weather
        		);



        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return response.trim().replaceAll("[^0-9:]", "");
    }

    /** 작성자 : 이승준
     *  작성일 : 2026-02-19(수정)
     *  현재시간이 출퇴근인지 아닌지
     */
    private boolean isRushHour(LocalDateTime dt) {
        int hour = dt.getHour();
        return (hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    /** 작성자 : 이승준
     *  작성일 : 2026-02-19(수정)
     *  혼합여부 판단
     */
    private String resolveTrafficType(Route route) {

        boolean hasBus = false;
        boolean hasSubway = false;

        for (RouteStep step : route.getSteps()) {
            if (step.getType() == RouteStepType.BUS) hasBus = true;
            if (step.getType() == RouteStepType.SUBWAY) hasSubway = true;
        }

        if (hasBus && hasSubway) return "혼합";
        if (hasBus) return "버스";
        if (hasSubway) return "지하철";
        return "기타";
    }
}
