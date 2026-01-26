package com.late.donot.weather.model.service;

import java.util.List;

import com.late.donot.api.dto.Weather;
import com.late.donot.api.dto.WeatherHour;
import com.late.donot.api.dto.WeekWeather;

public interface WeatherService {
	
	Weather mainWeatherDto(int nx, int ny, double lat, double lon);
	
	List<WeatherHour> getHourWeather(int nx, int ny);

	Weather mainWeatherRefresh(int nx, int ny, double lat, double lon);

	List<WeekWeather> getWeekWeather(int nx, int ny, double lat, double lon);

}
