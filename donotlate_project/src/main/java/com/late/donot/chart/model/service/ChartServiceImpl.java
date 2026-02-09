package com.late.donot.chart.model.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.late.donot.chart.client.WeekSubwayClient;
import com.late.donot.chart.model.mapper.ChartMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ChartServiceImpl implements ChartService {

    @Value("${chart.subway.week.api.key}")
    private String subwayApiKey;

    @Autowired
    private WeekSubwayClient weekSubwayClient; // Feign 주입

    @Autowired
    private ChartMapper mapper;

    // API 호출을 위한 객체 생성
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 작성자 : 유건우
	 * 작성일자 : 2026-02-07
	 * 상단 차트 내용 로드
	 */
    @Override
    public Map<String, Object> getTopChart() {
        return mapper.getTopChart();
    }

    @Override
    public List<Long> getWeeklySubwayCount() {
        List<Long> weeklyData = new ArrayList<>();
        // 서울시 API는 보통 2~3일 전 데이터가 최신입니다.
        LocalDate endDate = LocalDate.now().minusDays(8); 
        
        for (int i = 6; i >= 0; i--) {
            String targetDate = endDate.minusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            weeklyData.add(fetchDailyTotal(targetDate));
        }
        return weeklyData;
    }

    private Long fetchDailyTotal(String date) {
        try {
            // 서울 전체 역을 합산하기 위해 1번부터 1000번까지 호출 (서울 지하철역은 약 600개)
            String response = weekSubwayClient.getSubwayData(subwayApiKey, 1, 1000, date);
            
            JsonNode root = objectMapper.readTree(response);
            JsonNode rowArray = root.path("CardSubwayStatsNew").path("row");

            long dailyTotal = 0L;
            if (rowArray.isArray()) {
                for (JsonNode row : rowArray) {
                    // 승차인원 합산 (하차인원까지 포함하려면 ALIGHT_PASGR_NUM 추가)
                    dailyTotal += row.path("GTON_TNOPE").asLong();
                }
                log.info("{} 날짜 합산 완료: {}명", date, dailyTotal);
            } else {
                // 에러 코드 처리 (인증키 오류 등)
                log.error("{} API 응답 오류: {}", date, root.path("RESULT").path("MESSAGE").asText());
            }
            return dailyTotal;

        } catch (Exception e) {
            log.error("{} 호출 중 예외 발생: {}", date, e.getMessage());
            return 0L;
        }
    }
}
