package com.late.donot.calculator.model.service;

import java.time.LocalTime;

import com.late.donot.api.dto.DayType;
import com.late.donot.api.dto.TimeItem;

public interface SubwayScheduleService {
	
	TimeItem findNextSubway(int stationId, int wayCode, LocalTime baseTime, DayType dayType);
}
