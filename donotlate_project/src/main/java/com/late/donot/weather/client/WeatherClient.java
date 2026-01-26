package com.late.donot.weather.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="weatherClient", url="https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
public interface WeatherClient {

	@GetMapping("/getUltraSrtNcst")
	String getUltraSrtNcst(
			
			@RequestParam("serviceKey") String serviceKey,
			@RequestParam("pageNo") int pageNo,
			@RequestParam("numOfRows") int numOfRows,
			@RequestParam("dataType") String dataType,
			@RequestParam("base_date") String baseDate,
			@RequestParam("base_time") String baseTime,
			@RequestParam("nx") int nx,
			@RequestParam("ny") int ny
			);
	
	@GetMapping("/getVilageFcst")
	String getVilageFcst(
			
			@RequestParam("serviceKey") String serviceKey,
			@RequestParam("pageNo") int pageNo,
	        @RequestParam("numOfRows") int numOfRows,
	        @RequestParam("dataType") String dataType,
	        @RequestParam("base_date") String baseDate,
	        @RequestParam("base_time") String baseTime,
	        @RequestParam("nx") int nx,
	        @RequestParam("ny") int ny
			);
	
}
