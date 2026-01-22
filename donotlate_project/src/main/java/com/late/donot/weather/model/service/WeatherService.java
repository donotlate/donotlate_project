package com.late.donot.weather.model.service;

import com.late.donot.weather.model.dto.Weather;

public interface WeatherService {
	
	Weather mainWeatherDto(int nx, int ny, double lat, double lon);

}
