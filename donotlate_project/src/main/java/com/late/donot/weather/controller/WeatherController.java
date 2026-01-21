package com.late.donot.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.late.donot.weather.model.service.WeatherService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("weather")
@Slf4j
public class WeatherController {
	
	@Autowired
	private WeatherService service;
	
	

}
