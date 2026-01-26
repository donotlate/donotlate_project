package com.late.donot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekWeather {
	private String dayLabel;
    private String icon;
    private String condition;
    private int rainProb;
    private int minTemp;
    private int maxTemp;   
}
