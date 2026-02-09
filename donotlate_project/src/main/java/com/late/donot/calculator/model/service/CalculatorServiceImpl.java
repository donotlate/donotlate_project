package com.late.donot.calculator.model.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.api.dto.DayType;
import com.late.donot.api.dto.OdsayApi;
import com.late.donot.api.dto.OdsayPath;
import com.late.donot.api.dto.OdsayStation;
import com.late.donot.api.dto.OdsaySubPath;
import com.late.donot.api.dto.Route;
import com.late.donot.api.dto.RouteStep;
import com.late.donot.api.dto.RouteStepType;
import com.late.donot.calculator.client.OdsaylabClient;
import com.late.donot.common.config.AsyncConfig;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class CalculatorServiceImpl implements CalculatorService{

    private final AsyncConfig asyncConfig;
	
	@Autowired
	private OdsaylabClient odsayClient;
	
	@Value("${odsay.api.key}")
	private String apiKey;


    CalculatorServiceImpl(AsyncConfig asyncConfig) {
        this.asyncConfig = asyncConfig;
    }
	
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-03
	 *  Odsay 조회후 반환
	 */
	@Override
	public List<Route> findRoute(double sx, double sy, double ex, double ey, String mode, LocalTime departureTime, DayType dayType) {
		
		String raw = odsayClient.searchPubTransPathRaw(sx, sy, ex, ey, apiKey);
	    log.info("ODsay RAW = {}", raw);
		
		OdsayApi response = odsayClient.searchPubTransPath(sx, sy, ex, ey, apiKey);
		
		List<OdsayPath> paths = response.getResult().getPath();
		
		List<Route> routes = new ArrayList<>();
		
		for(OdsayPath path : paths) {
			
			if(!matchMode(path, mode)) continue;
		
			routes.add(convertToRoute(path, departureTime, dayType));
		}
		
		routes.sort(Comparator.comparingInt(Route::getTotalTime));
		
		return routes;
	}
	


	/** 작성자 : 이승준
	 *  작성일 : 2026-02-03
	 *  교통수단 종류 파악
	 */
	private boolean matchMode(OdsayPath path, String mode) {
		
		int bus = path.getInfo().getBusTransitCount();
		int subway = path.getInfo().getSubwayTransitCount();
		
		return switch(mode) {
			case "SUBWAY" -> bus == 0;
			case "BUS" -> subway == 0;
			case "MIX" -> bus > 0 && subway > 0;
			default -> true;
		};
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-03
	 *  Odsay 경로를 Route로 변환
	 */
	private Route convertToRoute(
	        OdsayPath path,
	        LocalTime baseDepartureTime,
	        DayType dayType
	) {

	    List<RouteStep> steps = new ArrayList<>();
	    LocalTime cursor = baseDepartureTime;

	    for (OdsaySubPath sp : path.getSubPath()) {

	        RouteStepType type = resolveType(sp);

	        List<String> stations = null;
	        if (sp.getPassStopList() != null &&
	            sp.getPassStopList().getStations() != null &&
	            sp.getPassStopList().getStations().size() >= 2) {

	            stations = sp.getPassStopList().getStations()
	                    .stream()
	                    .map(OdsayStation::getStationName)
	                    .toList();
	        }

	        LocalTime expectedDeparture = cursor;
	        LocalTime expectedArrival =
	                cursor.plusMinutes(sp.getSectionTime());

	        RouteStep.RouteStepBuilder builder = RouteStep.builder()
	                .type(type)
	                .title(buildTitle(sp, type))
	                .description(buildDescription(sp, type))
	                .time(sp.getSectionTime())
	                .expectedDepartureTime(expectedDeparture)
	                .expectedArrivalTime(expectedArrival);

	        if (type == RouteStepType.SUBWAY || type == RouteStepType.BUS) {
	            builder.stationCount(sp.getStationCount());
	            builder.stations(stations);
	        }

	        if (type == RouteStepType.BUS && sp.getLane() != null) {
	            List<String> busNames = sp.getLane().stream()
	                    .map(lane -> lane.getBusNo())
	                    .filter(no -> no != null && !no.isBlank())
	                    .toList();

	            builder.busNames(busNames);
	        }

	        RouteStep step = builder.build();
	        steps.add(step);

	        cursor = expectedArrival;
	    }

	    int transferCount =
	            path.getInfo().getBusTransitCount() +
	            path.getInfo().getSubwayTransitCount();

	    return Route.builder()
	            .totalTime(path.getInfo().getTotalTime())
	            .transferCount(transferCount)
	            .steps(steps)
	            .build();
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-03
	 *  이동구간 값을 Route로 변환
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
	 *  작성일 : 2026-02-03
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
	 *  작성일 : 2026-02-03
	 *  상세명 생성
	 */
	private String buildDescription(OdsaySubPath sp, RouteStepType type) {

        return switch (type) {
            case SUBWAY, BUS -> sp.getStartName() + " → " + sp.getEndName();
            case TRANSFER -> "환승 이동";
            case WALK -> "도보 이동";
        };
    }
	
}
