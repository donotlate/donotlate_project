package com.late.donot.calculator.model.service;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.api.dto.BusLane;
import com.late.donot.api.dto.BusStationInfoResponse;
import com.late.donot.api.dto.DayType;
import com.late.donot.api.dto.TimeItem;
import com.late.donot.calculator.client.OdsayBusTime;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BusScheduleServiceImpl implements BusScheduleService{
	
	@Autowired
	OdsayBusTime odsayBusTime;
	
	@Value("${odsay.api.key}")
    private String apiKey;
	
	@Override
	public TimeItem findNextBus(int stationId, String busNo, LocalTime baseTime, DayType dayType) {
		
		BusStationInfoResponse res = odsayBusTime.getBusStationInfo(apiKey, stationId, 0, "json");
		
		if (res == null || res.getResult() == null) return null;

        BusLane lane = res.getResult().getLane()
                .stream()
                .filter(l -> busNo.equals(l.getBusNo()))
                .findFirst()
                .orElse(null);

        if (lane == null) return null;

        LocalTime first = parseTime(lane.getBusFirstTime());
        LocalTime last  = parseTime(lane.getBusLastTime());
        int interval = parseInterval(lane.getBusInterval());

        if (first == null || last == null || interval <= 0) return null;

        LocalTime next = calcNextTime(first, last, interval, baseTime);

        if (next == null) return null;

        return TimeItem.builder()
                .departureTime(next.toString())
                .subwayClass(0)
                .endStationName(lane.getBusDirectionName())
                .build();
    }


    private LocalTime parseTime(String time) {
        if (time == null || time.isBlank()) return null;
        
        return LocalTime.parse(time);
    }

    private int parseInterval(String interval) {
        try {
            return Integer.parseInt(interval.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return -1;
        }
    }

    private LocalTime calcNextTime(LocalTime first, LocalTime last, int interval, LocalTime base) {

        if (base.isBefore(first)) {
            return first;
        }

        long diff = java.time.Duration.between(first, base).toMinutes();
        long cnt  = (diff / interval) + 1;

        LocalTime next = first.plusMinutes(cnt * interval);

        return next.isAfter(last) ? null : next;
    }


}
