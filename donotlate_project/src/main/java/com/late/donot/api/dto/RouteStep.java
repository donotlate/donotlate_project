package com.late.donot.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStep {
	
	private RouteStepType type;
    private String title;
    private String description;
    private int time;
    
    private Integer stationCount;
    private List<String> busNames;
    
    private List<String> stations;
}
