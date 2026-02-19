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
	public TimeItem findNextBus(int stationId,
	                            String busNo,
	                            LocalTime baseTime,
	                            DayType dayType) {

	    if (baseTime == null) baseTime = LocalTime.now();

	    BusStationInfoResponse res =
	            odsayBusTime.getBusStationInfo(apiKey, stationId, 0, "json");

	    if (res == null || res.getResult() == null) return null;

	    BusLane lane = res.getResult().getLane()
	            .stream()
	            .filter(l -> busNo.equals(l.getBusNo()))
	            .findFirst()
	            .orElse(null);

	    if (lane == null) return null;

	    LocalTime first = parseHHmm(lane.getBusFirstTime());
	    LocalTime last  = parseHHmm(lane.getBusLastTime());
	    int interval = parseInterval(lane.getBusInterval());

	    if (first == null || last == null || interval <= 0) return null;

	    LocalTime next = calcNextTime(first, last, interval, baseTime);

	    if (next == null) return null;

	    return TimeItem.builder()
	            .departureTime(formatHHmm(next))
	            .endStationName(lane.getBusDirectionName())
	            .build();
	}



	private LocalTime parseHHmm(String time) {

	    if (time == null || time.isBlank()) return null;

	    time = time.trim();

	    if (time.matches("^\\d{3,4}$")) {
	        if (time.length() == 3) {
	            time = "0" + time;
	        }
	        time = time.substring(0, 2) + ":" + time.substring(2);
	    }

	    String[] parts = time.split(":");
	    int hour = Integer.parseInt(parts[0]);
	    int minute = Integer.parseInt(parts[1]);

	    if (hour >= 24) {
	        hour = hour - 24;
	    }

	    return LocalTime.of(hour, minute);
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
    
    private String formatHHmm(LocalTime time) {
        if (time == null) return null;
        return time.getHour() + ":" + String.format("%02d", time.getMinute());
    }


}
