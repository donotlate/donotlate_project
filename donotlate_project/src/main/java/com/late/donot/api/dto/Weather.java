package com.late.donot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weather {

	private double temperature;
	private int humidity;
	private double windSpeed;
	private String condition;
	private double feelsLike;
	
	private String date;
	private String time;
	private String location;
	
	private Integer pm25;
    private String pmGrade;
    
    private Double precipitation;
    private Double snowfall;
}
