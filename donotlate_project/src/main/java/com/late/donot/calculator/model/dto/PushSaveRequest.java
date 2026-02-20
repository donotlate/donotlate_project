package com.late.donot.calculator.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushSaveRequest {
	
	private String pushName;
    private String transportType;
    private int arriveTime;
    private int prepareTime;
    private int spareTime;
    private int pushTime;
    private String dayOfWeek;

    private String startName;
    private double startLat;
    private double startLng;
    private String endName;
    private double endLat;
    private double endLng;

    private int memberNo;
    private int routeNo;
    
    private String startStation;
    private String endStation;
}
