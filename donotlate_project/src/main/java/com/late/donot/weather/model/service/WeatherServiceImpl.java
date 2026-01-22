package com.late.donot.weather.model.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.late.donot.weather.client.WeatherClient;
import com.late.donot.weather.model.dto.Weather;
import com.late.donot.weather.model.dto.WeatherApi;
import com.late.donot.weather.model.mapper.WeatherMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class WeatherServiceImpl implements WeatherService{

	@Autowired
	private WeatherMapper mapper;
	
	@Autowired
	private WeatherClient weatherClient;
	
	@Autowired
	private WeatherLocationService weatherLocationService;

	@Value("${weather.api.key}")
	private String serviceKey;
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  기상청 API 호출후 가공해서 반환
	 */
	@Override
	public Weather mainWeatherDto(int nx, int ny) {
		List<WeatherApi> items = callWeatherApi(nx,ny);
		
		Map<String, String> valueMap = items.stream()
				.collect(Collectors.toMap(
						WeatherApi :: getCategory, 
						WeatherApi :: getObsrValue,
						(a,b) -> a));
		
		WeatherApi first = items.get(0);
		String date = formatDate(first.getBaseDate());
		String time = formatTime(first.getBaseTime());
		
		String location = weatherLocationService.getLocationName(nx,ny);
				
		return Weather.builder()
				.temperature(parseDouble(valueMap.get("T1H")))
				.humidity(parseInt(valueMap.get("REH")))
				.windSpeed(parseDouble(valueMap.get("WSD")))
				.condition(resolveCondition(valueMap))
				.date(date)
				.time(time)
				.location(location)
				.build();
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *	날씨와 강수형태를 문자열 변환
	 */
	private String resolveCondition(Map<String, String> map) {
		String pty = map.get("PTY");
		
	    if (pty == null || "0".equals(pty)) return "맑음";
	    if ("1".equals(pty)) return "비";
	    if ("2".equals(pty)) return "비/눈";
	    if ("3".equals(pty)) return "눈";
	    if ("4".equals(pty)) return "소나기";
	    return "알수없음";
		
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  기상청 초단기API 호출, 반환
	 */
	private List<WeatherApi> callWeatherApi(int nx, int ny) {
		
		BaseDateTime base = getBaseDateTime();
		
		String response = weatherClient.getUltraSrtNcst(
				serviceKey,
				1,
				1000,
				"JSON",
				base.
				base.
				nx,
				ny);
		
		log.info("기상청 raw response = {}", response);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			JsonNode root = mapper.readTree(response);
			JsonNode itemsNode = root
					        .path("response")
					        .path("body")
					        .path("items")
					        .path("item");
			
			if(itemsNode.isMissingNode() || !itemsNode.isArray()) {
	            log.error("날씨 item 없음. 응답={}", response);
	            
	            return List.of(); 
	        }
			
			return mapper.convertValue(
							itemsNode,
							mapper.getTypeFactory()
							.constructCollectionType(List.class, WeatherApi.class));
			
			
		} catch (Exception e) {
			log.error("날씨 API 실패", e);
			return List.of();
		}
		
	}
	
	private BaseDateTime getBaseDateTime() {
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime base = now.minusMinutes(40);
		
		String baseDate = base.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String baseTime = base.format(DateTimeFormatter.ofPattern("HH")) + "00";
		
		return new BaseDateTime(baseDate, baseTime);
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  날짜 형식으로 변환
	 */
	private String formatDate(String baseDate) {
		
		return baseDate.substring(0,4) + "-" +
			   baseDate.substring(4,6) + "-" +
			   baseDate.substring(6,8);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  시간 형식으로 변환
	 */
	private String formatTime(String baseTime) {
		
		return baseTime.substring(0,2) + ":" +
		       baseTime.substring(2,4);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  문자열값 double로 반환
	 */
	private double parseDouble(String value) {
		
		return value == null ? 0.0 : Double.parseDouble(value);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  문자열값 int로 반환
	 */
	private int parseInt(String value) {
		
		return value == null ? 0 : Integer.parseInt(value);
	}
	
	
}
