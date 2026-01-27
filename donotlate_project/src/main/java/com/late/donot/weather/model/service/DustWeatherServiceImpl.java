package com.late.donot.weather.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.late.donot.api.dto.DustWeather;
import com.late.donot.weather.client.DustWeatherClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class DustWeatherServiceImpl implements DustWeatherService{
	
	@Autowired
    private DustWeatherClient dustWeatherClient;

    @Value("${dust.api.key}")
    private String dustServiceKey;

    /** 작성자 : 이승준
     *  작성일 : 2026-01-27
     *  미세먼지 로직
     */
    @Cacheable(value = "dustWeather", key = "'SEOUL'", unless = "#result == null")
    @Override
    public DustWeather getSeoulDust() {

        try {
            String response = dustWeatherClient.getSeoulAirQuality(
                dustServiceKey,
                "서울",
                1,
                100,
                "json",
                "1.0"
            );

            log.info("에어코리아 raw response = {}", response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            JsonNode items = root
                .path("response")
                .path("body")
                .path("items");

            if (!items.isArray() || items.isEmpty()) {
                log.warn("서울 미세먼지 데이터 없음");
                return null;
            }

            List<Integer> pm25List = new ArrayList<>();
            String dataTime = null;

            for (JsonNode item : items) {
                String pm25Str = item.path("pm25Value").asText();

                if (!"-".equals(pm25Str)) {
                    pm25List.add(Integer.parseInt(pm25Str));
                }

                if (dataTime == null) {
                    dataTime = item.path("dataTime").asText();
                }
            }

            if (pm25List.isEmpty()) {
                log.warn("pm25 유효 데이터 없음");
                return null;
            }

            int pm25 = pm25List.stream()
                               .mapToInt(Integer::intValue)
                               .max()
                               .orElse(0);

            return DustWeather.builder()
                .pm25(pm25)
                .grade(resolvePm25Grade(pm25))
                .dataTime(dataTime)
                .build();

        } catch (Exception e) {
            log.error("서울 미세먼지 조회 실패", e);
            return null;
        }
    }

    /** 작성자 : 이승준
     *  작성일 : 2026-01-27
     *  초미세먼지 등급 계산 
     */
    private String resolvePm25Grade(int pm25) {
        if (pm25 <= 15) return "매우 좋음";
        if (pm25 <= 35) return "좋음";
        if (pm25 <= 75) return "보통";
        if (pm25 <= 150) return "나쁨";
        return "매우 나쁨";
    }
}


