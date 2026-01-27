package com.late.donot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DustWeather {
	 private Integer pm25;
	 private Integer pm10;
	 private String grade;
	 private String dataTime;
}
