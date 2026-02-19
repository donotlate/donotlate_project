package com.late.donot.calculator.model.service;

import java.time.LocalTime;

import com.late.donot.api.dto.DayType;
import com.late.donot.api.dto.TimeItem;

public interface BusScheduleService {
	
	TimeItem findNextBus(int stationId, String busNo,LocalTime baseTime, DayType dayType);
}
