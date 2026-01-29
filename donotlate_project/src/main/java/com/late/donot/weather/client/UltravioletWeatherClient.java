package com.late.donot.weather.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
	    name = "ultravioletWeatherClient",
	    url = "https://apis.data.go.kr/1360000/LivingWthrIdxServiceV4"
	)
	public interface UltravioletWeatherClient {

	    @GetMapping("/getUVIdxV4")
	    String getUvIndex(
	        @RequestHeader("User-Agent") String userAgent,
	        @RequestParam("serviceKey") String serviceKey,
	        @RequestParam("pageNo") int pageNo,
	        @RequestParam("numOfRows") int numOfRows,
	        @RequestParam("dataType") String dataType,
	        @RequestParam("areaNo") String areaNo,
	        @RequestParam("time") String time
	    );
	}
