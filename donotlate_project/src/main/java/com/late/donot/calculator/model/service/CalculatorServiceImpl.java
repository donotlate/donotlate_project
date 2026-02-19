package com.late.donot.calculator.model.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;

import com.late.donot.api.dto.DayType;
import com.late.donot.api.dto.OdsayApi;
import com.late.donot.api.dto.OdsayPath;
import com.late.donot.api.dto.OdsayStation;
import com.late.donot.api.dto.OdsaySubPath;
import com.late.donot.api.dto.Route;
import com.late.donot.api.dto.RouteStep;
import com.late.donot.api.dto.RouteStepType;
import com.late.donot.api.dto.TimeItem;
import com.late.donot.calculator.client.OdsaylabClient;
import com.late.donot.common.config.AsyncConfig;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class CalculatorServiceImpl implements CalculatorService {

	@Autowired
	private AsyncConfig asyncConfig;

	@Autowired
	private OdsaylabClient odsayClient;

	@Autowired
	private SubwayScheduleService subwayScheduleService;

	@Autowired
	private BusScheduleService busScheduleService;

	@Value("${odsay.api.key}")
	private String apiKey;

	CalculatorServiceImpl(AsyncConfig asyncConfig) {
		this.asyncConfig = asyncConfig;
	}

	/** 작성자 : 이승준 
	 * 	작성일 : 2026-02-03 
	 * 	Odsay 조회 후 반환
	 */
	@Override
	public List<Route> findRoute(double sx, double sy, double ex, double ey, String mode, LocalTime departureTime,
			DayType dayType) {

		String raw = odsayClient.searchPubTransPathRaw(sx, sy, ex, ey, apiKey);
		log.info("ODsay RAW = {}", raw);

		OdsayApi response = odsayClient.searchPubTransPath(sx, sy, ex, ey, apiKey);

		List<OdsayPath> paths = response.getResult().getPath();
		List<Route> routes = new ArrayList<>();

		for (OdsayPath path : paths) {
			if (!matchMode(path, mode))
				continue;
			routes.add(convertToRoute(path, departureTime, dayType));
		}

		routes.sort(Comparator.comparingInt(Route::getTotalTime));
		return routes;
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 *  교통수단 종류 필터
	 */
	private boolean matchMode(OdsayPath path, String mode) {
		int bus = path.getInfo().getBusTransitCount();
		int subway = path.getInfo().getSubwayTransitCount();

		return switch (mode) {
		case "SUBWAY" -> bus == 0;
		case "BUS" -> subway == 0;
		case "MIX" -> bus > 0 && subway > 0;
		default -> true;
		};
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 * Odsay 경로 → Route 변환
	 */
	private Route convertToRoute(OdsayPath path, LocalTime baseDepartureTime, DayType dayType) {

		List<RouteStep> steps = new ArrayList<>();
		List<OdsaySubPath> subPaths = path.getSubPath();

		LocalTime cursor = baseDepartureTime != null ? baseDepartureTime : LocalTime.now();

		for (OdsaySubPath sp : subPaths) {

			RouteStepType type = resolveType(sp);

			List<String> stations = null;
			if (sp.getPassStopList() != null && sp.getPassStopList().getStations() != null
					&& sp.getPassStopList().getStations().size() >= 2) {

				stations = sp.getPassStopList().getStations().stream().map(OdsayStation::getStationName).toList();
			}

			LocalTime arriveTime = cursor;
			LocalTime departureTime = cursor;
			LocalTime expectedArrival;

			if (type == RouteStepType.SUBWAY) {

				int stationId = sp.getStartID();
				int wayCode = resolveWayCode(sp);

				TimeItem next = subwayScheduleService.findNextSubway(stationId, wayCode, arriveTime, dayType);

				if (next != null) {
					departureTime = parseTime(next.getDepartureTime());
				}

				expectedArrival = departureTime.plusMinutes(sp.getSectionTime());
			}

			else if (type == RouteStepType.BUS && sp.getLane() != null) {

				int stationId = sp.getStartID();
				String busNo = sp.getLane().get(0).getBusNo();

				TimeItem next = busScheduleService.findNextBus(stationId, busNo, arriveTime, dayType);

				if (next != null) {
					departureTime = parseTime(next.getDepartureTime());
				}

				expectedArrival = departureTime.plusMinutes(sp.getSectionTime());
			}

			else {
				expectedArrival = arriveTime.plusMinutes(sp.getSectionTime());
			}

			RouteStep.RouteStepBuilder builder = RouteStep.builder().type(type).title(buildTitle(sp, type))
					.description(buildDescription(sp, type)).time(sp.getSectionTime())
					.expectedDepartureTime(departureTime).expectedArrivalTime(expectedArrival);

			if (type == RouteStepType.SUBWAY || type == RouteStepType.BUS) {

				builder.stationCount(sp.getStationCount());
				builder.stations(stations);
			}

			if (type == RouteStepType.BUS && sp.getLane() != null) {

				List<String> busNames = sp.getLane().stream().map(lane -> lane.getBusNo())
						.filter(no -> no != null && !no.isBlank()).toList();

				builder.busNames(busNames);
			}

			steps.add(builder.build());
			cursor = expectedArrival;
		}

		int transferCount = path.getInfo().getBusTransitCount() + path.getInfo().getSubwayTransitCount();

		return Route.builder().totalTime(path.getInfo().getTotalTime()).transferCount(transferCount).steps(steps)
				.build();
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 *  API값 변환
	 */
	private RouteStepType resolveType(OdsaySubPath sp) {
		return switch (sp.getTrafficType()) {
		case 1 -> RouteStepType.SUBWAY;
		case 2 -> RouteStepType.BUS;
		case 3 -> {
			if (sp.getLane() == null || sp.getLane().isEmpty()) {
				yield RouteStepType.WALK;
			}
			yield RouteStepType.TRANSFER;
		}
		default -> RouteStepType.WALK;
		};
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 *  제목 생성
	 */
	private String buildTitle(OdsaySubPath sp, RouteStepType type) {
		return switch (type) {
		case SUBWAY, BUS -> sp.getLane().get(0).getName();
		case TRANSFER -> "환승";
		case WALK -> "도보";
		};
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 *  제목생성
	 */
	private String buildDescription(OdsaySubPath sp, RouteStepType type) {
		return switch (type) {
		case SUBWAY, BUS -> sp.getStartName() + " → " + sp.getEndName();
		case TRANSFER -> "환승 이동";
		case WALK -> "도보 이동";
		};
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 * 상행 / 하행 구분
	 */
	private int resolveWayCode(OdsaySubPath next) {

		Integer startId = next.getStartID();
		Integer endId = next.getEndID();

		if (startId == null || endId == null) {
			return 1;
		}

		return startId < endId ? 1 : 2;
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 *  시간대 API받아온거 변환
	 */
	private LocalTime parseTime(String time) {

	    if (time == null || time.isBlank()) {
	        return null;
	    }
	    if (time.length() == 4) {
	        time = "0" + time;
	    }

	    return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
	}
}
