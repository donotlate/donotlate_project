package com.late.donot.calculator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.late.donot.api.dto.OdsayApi;

@FeignClient(name="odsaylabClient", url="https://api.odsay.com")
public interface OdsaylabClient {
	
	@GetMapping("/v1/api/searchPubTransPathT")
	OdsayApi searchPubTransPath(
	        @RequestParam("SX") double sx,
	        @RequestParam("SY") double sy,
	        @RequestParam("EX") double ex,
	        @RequestParam("EY") double ey,
	        @RequestParam("apiKey") String apiKey
	    );
}
