package com.late.donot.chart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** 작성자 : 유건우
 * 작성일자 : 2026-02-09
 * 버스 요일별 이용자 수 api 통신 규격 선언
 */
@FeignClient(name = "seoulBusClient", url = "http://openapi.seoul.go.kr:8088")
public interface WeekBusClient {
    
    @GetMapping("/{key}/json/CardBusStatisticsServiceNew/{start}/{end}/{date}")
    String getBusData(
        @PathVariable("key") String key,
        @PathVariable("start") int start,
        @PathVariable("end") int end,
        @PathVariable("date") String date
    );
}
