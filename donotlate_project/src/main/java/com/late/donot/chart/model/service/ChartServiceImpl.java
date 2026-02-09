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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.late.donot.chart.client.WeekBusClient;
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
    private WeekSubwayClient weekSubwayClient;

    @Autowired
    private WeekBusClient weekBusClient;

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

    /** 작성자 : 유건우
	 * 작성일자 : 2026-02-09
	 * 지하철 요일별 이용자 수
	 */
    @Override
    public List<Long> getWeeklySubwayCount() {
        List<Long> weeklyData = new ArrayList<>();
        //저번주 (월~일) 데이터
        LocalDate endDate = LocalDate.now().minusDays(8); 
        
        for (int i = 6; i >= 0; i--) {
            String targetDate = endDate.minusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            weeklyData.add(fetchSubwayDailyTotal(targetDate));
        }
        return weeklyData;
    }

    /** 작성자 : 유건우
	 * 작성일자 : 2026-02-09
	 * 지하철 요일별 이용자 수 - 인원수 합산
     * (서울 지하철역은 약 600개)
	 */
    private Long fetchSubwayDailyTotal(String date) {
        try {
            // 지하철 API 서비스명: CardSubwayStatsNew
            String response = weekSubwayClient.getSubwayData(subwayApiKey, 1, 1000, date);
            
            JsonNode root = objectMapper.readTree(response);
            // 지하철 API 응답 계층 구조에 맞춰 경로 설정
            JsonNode rowArray = root.path("CardSubwayStatsNew").path("row");

            long dailyTotal = 0L;
            if (rowArray.isArray()) {
                for (JsonNode row : rowArray) {
                    //승차인원 합산
                    dailyTotal += row.path("GTON_TNOPE").asLong();
                }
                log.info("지하철 {} 날짜 합산 완료: {}명", date, dailyTotal);
            }
            return dailyTotal;
        } catch (Exception e) {
            log.error("지하철 데이터 호출 중 오류: {}", e.getMessage());
            return 0L;
        }
    }

    /** 작성자 : 유건우
	 * 작성일자 : 2026-02-09
	 * 버스 요일별 이용자 수 
     * (정류장 갯수가 약 4만개라 상위 1000개 정류장 기준으로 채택)
	 */
    @Override
    public List<Long> getWeeklyBusCount() {
        List<Long> weeklyData = new ArrayList<>();
        //저번주 (월~일) 데이터
        LocalDate endDate = LocalDate.now().minusDays(8); 
        
        for (int i = 6; i >= 0; i--) {
            String targetDate = endDate.minusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            weeklyData.add(fetchBusDailyTotal(targetDate));
        }
        return weeklyData;
    }

    /** 작성자 : 유건우
	 * 작성일자 : 2026-02-09
	 * 버스 요일별 이용자 수 - 인원수 합산
	 */
    private Long fetchBusDailyTotal(String date) {
        try {
            // 버스 API 서비스명: CardBusStatisticsServiceNew
            String response = weekBusClient.getBusData(subwayApiKey, 1, 1000, date);
            
            JsonNode root = objectMapper.readTree(response);
            // 버스 API 응답 계층 구조에 맞춰 경로 설정
            JsonNode rowArray = root.path("CardBusStatisticsServiceNew").path("row");

            long dailyTotal = 0L;
            if (rowArray.isArray()) {
                for (JsonNode row : rowArray) {
                    //승차인원 합산
                    dailyTotal += row.path("GTON_TNOPE").asLong();
                }
                log.info("버스 {} 날짜 합산 완료: {}명", date, dailyTotal);
            }
            return dailyTotal;
        } catch (Exception e) {
            log.error("버스 데이터 호출 중 오류: {}", e.getMessage());
            return 0L;
        }
    }
}
