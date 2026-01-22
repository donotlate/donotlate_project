package com.late.donot.weather.model.service;

import org.springframework.stereotype.Service;

@Service
public class WeatherLocationServiceImpl implements WeatherLocationService{

	@Override
	public String getLocationName(int nx, int ny) {
		
		return "서울특별시 종로구";
	}

}
