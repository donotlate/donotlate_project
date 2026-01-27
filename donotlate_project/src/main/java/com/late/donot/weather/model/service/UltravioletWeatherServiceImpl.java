//package com.late.donot.weather.model.service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.late.donot.api.dto.UltravioletWeather;
//import com.late.donot.weather.client.UltravioletWeatherClient;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Transactional(rollbackFor = Exception.class)
//@Slf4j
//public class UltravioletWeatherServiceImpl implements UltravioletWeatherService {
//
//    @Autowired
//    private UltravioletWeatherClient uvClient;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Value("${ultraviolet.api.key}")
//    private String serviceKey;
//
//    private static final String DATA_TYPE = "JSON";
//    private static final int PAGE_NO = 1;
//    private static final int NUM_OF_ROWS = 10;
//
//    /** 작성자 : 이승준
//     *  작성일 : 2026-01-27
//     *  자외선 지수 조회
//     */
//    @Override
//    public UltravioletWeather getTodayUv(String areaNo) {
//
//        try {
//            // ✅ YYYYMMDDHHmm 형식 (예: 202601270600)
//            String time = LocalDateTime.now()
//                    .withHour(6)
//                    .withMinute(0)
//                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
//
//            log.info("자외선 API time 파라미터 = {}", time);
//
//            String response = uvClient.getUvIndex(
//                    serviceKey,
//                    PAGE_NO,
//                    NUM_OF_ROWS,
//                    DATA_TYPE,
//                    areaNo,
//                    time
//            );
//
//            log.info("자외선 raw response = {}", response);
//
//            JsonNode item = objectMapper.readTree(response)
//                    .path("response")
//                    .path("body")
//                    .path("items")
//                    .path("item")
//                    .get(0);
//
//            int uv = item.path("today").asInt(-1);
//
//            return UltravioletWeather.builder()
//                    .uvIndex(uv)
//                    .level(uvLevel(uv))
//                    .build();
//
//        } catch (Exception e) {
//            log.error("자외선 지수 조회 실패", e);
//            throw new RuntimeException("자외선 지수 조회 실패", e);
//        }
//    }
//
//    /** 자외선 단계 계산 */
//    private String uvLevel(int uv) {
//        if (uv < 0) return "-";
//        if (uv < 3) return "낮음";
//        if (uv < 6) return "보통";
//        if (uv < 8) return "높음";
//        if (uv < 11) return "매우높음";
//        return "위험";
//    }
//}
//

