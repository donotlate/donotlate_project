package com.late.donot.weather.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dustWeatherClient", url = "https://apis.data.go.kr/B552584")
	public interface DustWeatherClient {

	    @GetMapping("/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty")
	    String getSeoulAirQuality(
	        @RequestParam("serviceKey") String serviceKey,
	        @RequestParam("sidoName") String sidoName, // "서울"
	        @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
	        @RequestParam(value = "numOfRows", defaultValue = "100") int numOfRows,
	        @RequestParam(value = "returnType", defaultValue = "json") String returnType,
	        @RequestParam(value = "ver", defaultValue = "1.0") String ver
	    );
	}