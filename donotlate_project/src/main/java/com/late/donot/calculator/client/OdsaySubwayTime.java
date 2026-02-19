package com.late.donot.calculator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.late.donot.api.dto.SubwayTimeResponse;

@FeignClient(name="OdsaySubwayTime" , url="https://api.odsay.com")
public interface OdsaySubwayTime {

	@GetMapping("/v1/api/searchSubwaySchedule")
	SubwayTimeResponse getTime(
			 @RequestParam("apiKey") String apiKey,
		     @RequestParam("stationID") int stationId,
		     @RequestParam(value = "wayCode", required = false) Integer wayCode,
		     @RequestParam(value = "lang", defaultValue = "0") int lang,
		     @RequestParam(value = "output", defaultValue = "json") String output);
	
	
}
