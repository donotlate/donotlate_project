package com.late.donot.weather.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.weather.client.WeatherClient;
import com.late.donot.weather.model.dto.Weather;
import com.late.donot.weather.model.mapper.WeatherMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class WeatherServiceImpl implements WeatherService{

	@Autowired
	private WeatherMapper mapper;
	
	@Autowired
	private WeatherClient weatherClient;

	@Value("${weather.api.key}")
	private String servicveKey;
	
	@Override
	public Weather mainWeatherDto(int nx, int ny) {
		
		
		
		return null;
	}
}
