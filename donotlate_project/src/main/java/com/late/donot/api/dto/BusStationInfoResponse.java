package com.late.donot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusStationInfoResponse {
	private Result result;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class Result {
		private String stationName;
        private int stationID;
        private java.util.List<BusLane> lane;
	}
	
}
