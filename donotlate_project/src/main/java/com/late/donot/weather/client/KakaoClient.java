package com.late.donot.weather.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="kakaoClient", url="https://dapi.kakao.com")
public interface KakaoClient {
	
	@GetMapping("/v2/local/geo/coord2address.json")
	String coordToAddress(@RequestHeader("Authorization") String authorization,
			              @RequestParam("x") double lon,
			              @RequestParam("y") double lat);

}
