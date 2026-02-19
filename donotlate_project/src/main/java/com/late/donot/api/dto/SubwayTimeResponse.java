package com.late.donot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubwayTimeResponse {
	
	private Result result;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class Result{
		private ScheduleGroup weekdaySchedule;
        private ScheduleGroup saturdaySchedule;
        private ScheduleGroup holidaySchedule;
	}
}
