package com.late.donot.calculator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.late.donot.api.dto.BusStationInfoResponse;

@FeignClient(name="odsayBusTime", url = "https://api.odsay.com")
public interface OdsayBusTime {
	
	@GetMapping("/v1/api/busStationInfo")
	BusStationInfoResponse getBusStationInfo(
		    @RequestParam("apiKey") String apiKey,
		    @RequestParam("stationID") int stationId,
		    @RequestParam(value = "lang", defaultValue = "0") int lang,
		    @RequestParam(value = "output", defaultValue = "json") String output
		    );
}
