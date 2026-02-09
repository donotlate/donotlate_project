package com.late.donot.calculator.model.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.api.dto.DayType;
import com.late.donot.api.dto.ScheduleGroup;
import com.late.donot.api.dto.SubwayTimeResponse;
import com.late.donot.api.dto.TimeItem;
import com.late.donot.calculator.client.OdsaySubwayTime;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SubwayScheduleServiceImpl implements SubwayScheduleService{
	
	@Autowired
	private OdsaySubwayTime odsaySubwayTime;
	
	@Value("${odsay.api.key}")
	private String apiKey;
	
	@Override
	public TimeItem findNextSubway(int stationId, int wayCode, LocalTime baseTime, DayType dayType) {
	
		SubwayTimeResponse res = odsaySubwayTime.getTime(apiKey, stationId, wayCode, 0, "json");
		
		List<TimeItem> list =  extractByDay(res, dayType, wayCode);
		
		return list.stream().filter(t -> LocalTime.parse(t.getDepartureTime()).isAfter(baseTime))
							.findFirst()
							.orElse(null);
	}
	
	private List<TimeItem> extractByDay(SubwayTimeResponse res, DayType dayType, int wayCode) {
		
		ScheduleGroup group = switch(dayType) {
			case WEEKDAY -> res.getResult().getWeekdaySchedule();
			case SATURDAY -> res.getResult().getSaturdaySchedule();
			case HOLIDAY -> res.getResult().getHolidaySchedule();
		};
		
		return wayCode == 1 ? group.getUp() : group.getDown();
	}
	
	

}
