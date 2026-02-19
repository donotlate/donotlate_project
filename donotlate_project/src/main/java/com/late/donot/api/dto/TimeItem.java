package com.late.donot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeItem {
	private String departureTime;
    private int subwayClass;
    private String endStationName;
}
