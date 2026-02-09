package com.late.donot.chart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "seoulSubwayClient", url = "http://openapi.seoul.go.kr:8088")
public interface WeekSubwayClient {

	@GetMapping("/{key}/json/CardSubwayStatsNew/{start}/{end}/{date}")
    String getSubwayData(
        @PathVariable("key") String key,
        @PathVariable("start") int start,
        @PathVariable("end") int end,
        @PathVariable("date") String date
    );
}
