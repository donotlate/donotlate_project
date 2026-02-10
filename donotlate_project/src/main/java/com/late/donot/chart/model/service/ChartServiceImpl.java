package com.late.donot.chart.model.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.late.donot.chart.client.DistanceClient;
import com.late.donot.chart.client.TransferClient;
import com.late.donot.chart.client.WeekBusClient;
import com.late.donot.chart.client.WeekSubwayClient;
import com.late.donot.chart.model.mapper.ChartMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ChartServiceImpl implements ChartService {

    @Value("${chart.traffic.week.api.key}")
    private String subwayApiKey;

    @Value("${chart.subway.transfer.api.key}")
    private String transferApiKey;

    @Autowired
    private WeekSubwayClient weekSubwayClient;

    @Autowired
    private WeekBusClient weekBusClient;

    @Autowired
    private TransferClient transferClient;

    @Autowired
    private DistanceClient distanceClient;

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

    /** 작성자 : 유건우
	 * 작성일자 : 2026-02-10
	 * 환승 많은 노선 Top 10
	 */
    @Override
    public List<Map<String, Object>> getTransferCount() {
        try {
            // FeignClient를 통해 데이터 호출
            String response = transferClient.getTransferData(1, 10, transferApiKey);
            
            JsonNode root = objectMapper.readTree(response);
            JsonNode dataArray = root.path("data");
    
            List<Map<String, Object>> result = new ArrayList<>();
            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    Map<String, Object> map = new HashMap<>();
                    // API 필드명에 맞춰 매핑
                    map.put("station", node.path("역명").asText());
                    map.put("count", node.path("평일(일평균)").asLong());
                    result.add(map);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("환승 데이터 파싱 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /** 작성자 : 유건우
	 * 작성일자 : 2026-02-10
	 * 역 간 거리 긴 구간 Top 10
	 */
    @Override
    public List<Map<String, Object>> getStationDistance() {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // 상위 8개만 가져오기
            String response = distanceClient.getDistanceData(1, 10, transferApiKey);
            JsonNode root = objectMapper.readTree(response);
            JsonNode dataArray = root.path("data");

            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    Map<String, Object> map = new HashMap<>();
                    
                    // 역간 정보 (예: 강남-양재)
                    // API 필드명에 맞춰 "역명" 혹은 "구간" 확인 필요 (Swagger 기준 "역명" 사용 가능성 높음)
                    map.put("section", node.path("역명").asText());

                    // 거리 정보 (소수점이 포함된 거리 데이터)
                    double distance = node.path("역간거리").asDouble();
                    
                    map.put("distance", distance);
                    result.add(map);
                }
            }
        } catch (Exception e) {
            log.error("역간 거리 데이터 로드 실패: {}", e.getMessage());
        }
        return result;
    }
}
