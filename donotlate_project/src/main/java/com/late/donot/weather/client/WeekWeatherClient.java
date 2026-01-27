package com.late.donot.weather.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weekWeatherClient", url = "http://apis.data.go.kr/1360000/MidFcstInfoService")
public interface WeekWeatherClient {

    @GetMapping("/getMidLandFcst")
    String getMidLandFcst(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("numOfRows") int numOfRows,
            @RequestParam("dataType") String dataType,
            @RequestParam("regId") String regId,
            @RequestParam("tmFc") String tmFc
    );

    @GetMapping("/getMidTa")
    String getMidTa(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("numOfRows") int numOfRows,
            @RequestParam("dataType") String dataType,
            @RequestParam("regId") String regId,
            @RequestParam("tmFc") String tmFc
    );
}