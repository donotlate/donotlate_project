package com.late.donot.chart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "distanceClient", url = "https://api.odcloud.kr/api/15081860/v1")
public interface DistanceClient {

    @GetMapping("/uddi:4d9ddd46-f2fe-4521-8c6f-d33cd8579cad")
    String getDistanceData(
        @RequestParam("page") int page,
        @RequestParam("perPage") int perPage,
        @RequestParam("serviceKey") String serviceKey
    );
}