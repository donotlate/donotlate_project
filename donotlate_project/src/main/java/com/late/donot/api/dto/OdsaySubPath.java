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
public class OdsaySubPath {
	
	private int trafficType;
    private int sectionTime;

    private List<OdsayLane> lane;

    private String startName;
    private String endName;
}
