package com.late.donot.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.late.donot.weather.model.dto.Weather;
import com.late.donot.weather.model.service.WeatherService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("weather")
@Slf4j
public class WeatherController {
	
	@Autowired
	private WeatherService service;
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  메인 날씨 화면
	 */
	@GetMapping("/main")
	public Weather MainWeather(@RequestParam("nx") int nx, 
							   @RequestParam("ny") int ny) {
		
		return service.mainWeatherDto(nx, ny);
	}
	
	

}
