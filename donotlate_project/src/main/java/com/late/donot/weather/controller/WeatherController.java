package com.late.donot.weather.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.late.donot.api.dto.CoordinatePoint;
import com.late.donot.api.dto.DustWeather;
import com.late.donot.api.dto.UltravioletWeather;
import com.late.donot.api.dto.Weather;
import com.late.donot.api.dto.WeatherHour;
import com.late.donot.api.dto.WeekWeather;
import com.late.donot.weather.model.service.DustWeatherService;
import com.late.donot.weather.model.service.UltravioletWeatherService;
import com.late.donot.weather.model.service.WeatherService;
import com.late.donot.weather.util.CoordinateConverter;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("weather")
@Slf4j
public class WeatherController {
	
	@Autowired
	private WeatherService service;
	
	@Autowired
	private DustWeatherService dustService;
	
	@Autowired
	private UltravioletWeatherService ultravioletService;
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  메인 날씨 화면
	 */
	@GetMapping("/main")
	public Weather mainWeather(@RequestParam("lat") double lat, 
							   @RequestParam("lon") double lon,
							   @RequestParam(name = "refresh", required = false, defaultValue = "false") boolean refresh) {	
		
		CoordinatePoint coordinate = CoordinateConverter.toCoordinate(lat,lon);
		
		if(refresh) {
			return service.mainWeatherRefresh(coordinate.getNx(), coordinate.getNy(), lat, lon);
		}
		
		return service.mainWeatherDto(coordinate.getNx(), coordinate.getNy(), lat, lon);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  시간대별 날씨
	 */
	@GetMapping("/hour")
	public List<WeatherHour> getHourWeather(@RequestParam("lat") double lat,
										    @RequestParam("lon") double lon) {
		
		CoordinatePoint coordinate = CoordinateConverter.toCoordinate(lat, lon);
		
		log.info("시간별 날씨 조회 lat={}, lon={} → nx={}, ny={}",
	              lat, lon,coordinate.getNx(), coordinate.getNy());

        return service.getHourWeather(coordinate.getNx(),
                				      coordinate.getNy());
		
	}
	
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  주간 날씨 
	 */
	@GetMapping("/week")
	public List<WeekWeather> getWeekWeather(@RequestParam("lat") double lat,
	        								@RequestParam("lon") double lon) {

	    CoordinatePoint coordinate =
	        CoordinateConverter.toCoordinate(lat, lon);

	    log.info("주간 날씨 조회 lat={}, lon={} → nx={}, ny={}",
	             lat, lon, coordinate.getNx(), coordinate.getNy());

	    return service.getWeekWeather(
	            coordinate.getNx(),
	            coordinate.getNy(),
	            lat,
	            lon
	    );
	}
	
	 /** 작성자 : 이승준
	  *  작성일 : 2026-01-27
	  *  미세먼지
	 */
	@GetMapping("/dust")
	public Weather getMainWeatherWithDust(@RequestParam("lat") double lat,
	        							  @RequestParam("lon") double lon,
	        							  @RequestParam(value = "refresh", defaultValue = "false") boolean refresh) {
		CoordinatePoint point = CoordinateConverter.toCoordinate(lat, lon);
		int nx = point.getNx();
		int ny = point.getNy();
		
	    Weather weather = refresh
	        ? service.mainWeatherRefresh(nx, ny, lat, lon)
	        : service.mainWeatherDto(nx, ny, lat, lon);

	    DustWeather dust = dustService.getSeoulDust();
	    if (dust != null) {
	        weather.setPm25(dust.getPm25());
	        weather.setPmGrade(dust.getGrade());
	    }

	    return weather;
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-27
	 *  자외선
	 */
	@GetMapping("/ultraviolet")
    public UltravioletWeather getUltraviolet(
            @RequestParam("areaNo") String areaNo
    ) {
        return ultravioletService.getTodayUv(areaNo);
    }

}


