package com.late.donot.main.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushSimpleDTO {

	private int pushNo;
    private String pushName;
    private int pushTime;
    private String dayOfWeek;
    private String transportType;
    
    private String startName;
    private String endName;
    private String startStation;
    private String endStation;
    
    private String isActive;
}
