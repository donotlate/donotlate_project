package com.late.donot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusLane {
	
	private String busNo;
    private String busFirstTime;
    private String busLastTime;
    private String busInterval;
    private int busStationIdx;
    private String busDirectionName;
}
