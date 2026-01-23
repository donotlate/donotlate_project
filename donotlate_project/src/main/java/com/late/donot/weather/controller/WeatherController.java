package com.late.donot.weather.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.late.donot.api.dto.CoordinatePoint;
import com.late.donot.api.dto.Weather;
import com.late.donot.api.dto.WeatherHour;
import com.late.donot.weather.model.service.WeatherService;
import com.late.donot.weather.uitl.CoordinateConverter;

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
	public Weather mainWeather(@RequestParam("lat") double lat, 
							   @RequestParam("lon") double lon) {	
		
		CoordinatePoint coordinate = CoordinateConverter.toCoordinate(lat,lon);
		
		return service.mainWeatherDto(coordinate.getNx(), coordinate.getNy(), lat, lon);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  시간대별 날씨
	 */
	@GetMapping("/hour")
	public List<WeatherHour> getHourWeather(@RequestParam("lat") double lat,
										    @RequestParam("lon") double lon) {
		
		CoordinatePoint coordinate = CoordinateConverter.toCoordinate(lat, lon);
		
		log.info("시간별 날씨 조회 lat={}, lon={} → nx={}, ny={}",
	              lat, lon,coordinate.getNx(), coordinate.getNy());

        return service.getHourWeather(coordinate.getNx(),
                				      coordinate.getNy());
		
	}
	
	

}
