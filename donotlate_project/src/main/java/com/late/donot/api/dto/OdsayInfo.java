package com.late.donot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OdsayInfo {
	
	private int totalTime;
    private int payment;
    private int busTransitCount;
    private int subwayTransitCount;
    private int totalWalkTime;  
}
