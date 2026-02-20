package com.late.donot.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Route {

	private int totalTime;
    private int transferCount;
    private List<RouteStep> steps;
    
    private String firstStation;
    private String lastStation;
}
