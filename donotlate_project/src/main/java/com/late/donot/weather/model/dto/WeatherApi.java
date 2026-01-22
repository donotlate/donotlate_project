package com.late.donot.weather.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApi {
	
	@JsonProperty("category")
	private String category;
	
	@JsonProperty("obsrValue")
	private String obsrValue;
	
	@JsonProperty("baseDate")
	private String baseDate;
	
	@JsonProperty("baseTime")
	private String baseTime;
	

}
