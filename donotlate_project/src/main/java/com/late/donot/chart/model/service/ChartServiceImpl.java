package com.late.donot.chart.model.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 작성자 : 유건우
     * 작성일자 : 2026-02-11
     * [공통 유틸 메소드] JSON 응답에서 안전하게 특정 노드 배열(row/data) 추출
     */
    private JsonNode getSafeRowNode(String response, String rootName, String arrayName) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode targetNode = rootName != null ? root.path(rootName).path(arrayName) : root.path(arrayName);
            
            if (targetNode.isArray() && targetNode.size() > 0) {
                return targetNode;
            }
        } catch (Exception e) {
            log.warn("JSON 파싱 중 경고(데이터 없음 또는 형식 불일치): {}", e.getMessage());
        }
        return objectMapper.createArrayNode(); // 빈 배열 반환
    }

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
     * 지하철 요일별 이용자 수 (일주일 전 기준)
     */
    @Override
    public List<Long> getWeeklySubwayCount() {
        List<Long> weeklyData = new ArrayList<>();
        LocalDate endDate = LocalDate.now().minusDays(8);
        
        for (int i = 6; i >= 0; i--) {
            String targetDate = endDate.minusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            weeklyData.add(fetchDailyTotal(targetDate, "subway"));
        }
        return weeklyData;
    }

    /** 작성자 : 유건우
     * 작성일자 : 2026-02-09
     * 버스 요일별 이용자 수 (일주일 전 기준)
     */
    @Override
    public List<Long> getWeeklyBusCount() {
        List<Long> weeklyData = new ArrayList<>();
        LocalDate endDate = LocalDate.now().minusDays(8);
        
        for (int i = 6; i >= 0; i--) {
            String targetDate = endDate.minusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            weeklyData.add(fetchDailyTotal(targetDate, "bus"));
        }
        return weeklyData;
    }

    /** 작성자 : 유건우
     * 작성일자 : 2026-02-09
     * [공용 로직] 일자별 총 승차 인원 합계 계산 (버스/지하철 공통 처리)
     */
    private Long fetchDailyTotal(String date, String type) {
        try {
            String response = "";
            String rootName = "";
            
            if ("subway".equals(type)) {
                response = weekSubwayClient.getSubwayData(subwayApiKey, 1, 1000, date);
                rootName = "CardSubwayStatsNew";
            } else {
                response = weekBusClient.getBusData(subwayApiKey, 1, 1000, date);
                rootName = "CardBusStatisticsServiceNew";
            }

            JsonNode rows = getSafeRowNode(response, rootName, "row");
            
            // Stream을 이용한 합계 계산
            return StreamSupport.stream(rows.spliterator(), false)
                    .mapToLong(row -> row.path("GTON_TNOPE").asLong(0))
                    .sum();

        } catch (Exception e) {
            log.error("{} 데이터 합산 중 에러 (날짜: {}): {}", type, date, e.getMessage());
            return 0L;
        }
    }

    /** 작성자 : 유건우
     * 작성일자 : 2026-02-10
     * 환승 많은 노선 Top 10
     * API 토큰 제한으로 인해 100개만 호출
     */
    @Override
    public List<Map<String, Object>> getTransferCount() {
        try {
            String response = transferClient.getTransferData(1, 100, transferApiKey);
            JsonNode rows = getSafeRowNode(response, null, "data");

            return StreamSupport.stream(rows.spliterator(), false)
                    .map(node -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("station", node.path("역명").asText());
                        map.put("count", node.path("평일(일평균)").asLong(0));
                        return map;
                    })
                    .sorted((m1, m2) -> Long.compare((long) m2.get("count"), (long) m1.get("count"))) // 내림차순 정렬
                    .limit(10) // 상위 10개
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("환승 많은 역 데이터 호출 중 오류: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /** 작성자 : 유건우
     * 작성일자 : 2026-02-11 
     * 역 간 거리 긴 구간 Top 10
     * (수정: 호출 데이터 1000개 확대 및 정확한 정렬 적용)
     */
    @Override
    public List<Map<String, Object>> getStationDistance() {
        try {
            // 1. 데이터 풀을 1000개로 대폭 확대
            String response = distanceClient.getDistanceData(1, 1000, transferApiKey);
            JsonNode rows = getSafeRowNode(response, null, "data");

            // 2. 중복 제거를 위한 Map (역명을 키로 사용)
            Map<String, Map<String, Object>> distinctMap = new HashMap<>();

            for (JsonNode node : rows) {
                String stationName = node.path("역명").asText("");
                double distance = node.path("역간거리").asDouble(0.0);

                if (stationName.isEmpty()) continue;

                // 동일 역명이 이미 있다면, 더 긴 거리의 데이터로 업데이트 (정확한 Top 10을 위해)
                if (!distinctMap.containsKey(stationName) || 
                    (double)distinctMap.get(stationName).get("distance") < distance) {
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("section", stationName);
                    map.put("distance", distance);
                    distinctMap.put(stationName, map);
                }
            }

            // 3. 1000개 데이터 중 중복 제거된 역들을 '거리순'으로 다시 정렬하여 상위 10개 추출
            return distinctMap.values().stream()
                    .sorted((m1, m2) -> Double.compare((double) m2.get("distance"), (double) m1.get("distance")))
                    .limit(10)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("역 간 거리 긴 구간 Top 10 데이터 호출 중 오류: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /** 작성자 : 유건우
     * 작성일자 : 2026-02-11
     * 지하철 혼잡도 통계 (상위 8개 역) - 출퇴근 구분
     */
    @Override
    public List<Map<String, Object>> getSubwayCongestion(String type) {
        String targetDate = LocalDate.now().minusDays(8).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        try {
            String response = weekSubwayClient.getSubwayData(subwayApiKey, 1, 1000, targetDate);
            JsonNode rows = getSafeRowNode(response, "CardSubwayStatsNew", "row");

            return StreamSupport.stream(rows.spliterator(), false)
                    .map(row -> {
                        Map<String, Object> map = new HashMap<>();
                        String stationName = row.path("SBWY_STNS_NM").asText("");
                        
                        long count = 0;
                        if ("attendance".equals(type)) {
                            count = row.path("GTON_TNOPE").asLong(0); // 승차
                        } else {
                            count = row.path("GTOFF_TNOPE").asLong(0); // 하차
                        }

                        map.put("station", stationName);
                        map.put("count", count);
                        return map;
                    })
                    .filter(map -> !map.get("station").toString().isEmpty()) // 역 이름 없는 더미 데이터 제외
                    .sorted((m1, m2) -> Long.compare((long) m2.get("count"), (long) m1.get("count"))) // 인원수 내림차순
                    .limit(8) // 상위 8개만 추출
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("혼잡도 데이터(타입:{}) 호출 중 오류: {}", type, e.getMessage());
            return new ArrayList<>();
        }
    }
}