package com.late.donot.weather.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.late.donot.api.dto.BaseDateTime;
import com.late.donot.api.dto.Weather;
import com.late.donot.api.dto.WeatherApi;
import com.late.donot.api.dto.WeatherHour;
import com.late.donot.api.dto.WeatherHourApi;
import com.late.donot.api.dto.WeekWeather;
import com.late.donot.weather.client.WeatherClient;
import com.late.donot.weather.client.WeekWeatherClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class WeatherServiceImpl implements WeatherService{
	
	@Autowired
	private WeatherClient weatherClient;
	
	@Autowired
	private WeekWeatherClient weekWeatherClient;
	
	@Autowired
	private WeatherLocationService weatherLocationService;

	@Value("${weather.api.key}")
	private String serviceKey;
	
	@Value("${weather.week.api.key}")
	private String weekServiceKey;
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  기상청 API 호출후 가공해서 반환
	 */
	@Cacheable(value = "weatherMain", key = "#nx + ':' + #ny", unless = "#result == null")
	@Override
	public Weather mainWeatherDto(int nx, int ny, double lat, double lon) {
		List<WeatherApi> items = callWeatherApi(nx,ny);
		
		Map<String, String> valueMap = items.stream()
				.collect(Collectors.toMap(
						WeatherApi :: getCategory, 
						WeatherApi :: getObsrValue,
						(a,b) -> a));
		
		String pty = valueMap.get("PTY");

		Double precipitation = null;
		Double snowfall = null;

		if ("1".equals(pty) || "4".equals(pty)) {
		    precipitation = parseDoubleSafe(valueMap.get("RN1"));
		}

		if ("3".equals(pty)) {
		    snowfall = parseDoubleSafe(valueMap.get("SNO"));
		}
		
		WeatherApi first = items.get(0);
		String date = formatDate(first.getBaseDate());
		String time = formatTime(first.getBaseTime());
		
		String location = weatherLocationService.getLocationName(lat,lon);
		
		double temp = parseDouble(valueMap.get("T1H"));
	    double wind = parseDouble(valueMap.get("WSD"));
	    double feelsLike = calculateFeelslike(temp, wind);
				
	    return Weather.builder()
	    	    .temperature(temp)
	    	    .feelsLike(feelsLike)
	    	    .humidity(parseInt(valueMap.get("REH")))
	    	    .windSpeed(parseDouble(valueMap.get("WSD")))
	    	    .condition(resolveCondition(valueMap))
	    	    .precipitation(precipitation)
	    	    .snowfall(snowfall)
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
				base.getBaseDate(),
				base.getBaseTime(),
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
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  시간대 설정
	 */
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
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  체감온도 계산식
	 */
	private double calculateFeelslike(double temp, double windSpeed) {
		
		if(windSpeed < 1.3) {
			return temp;
		}
		
		double windKmh = windSpeed * 3.6;
		
		double feelslike = 13.12 + 0.6215*temp - 11.37*Math.pow(windKmh, 0.16) +0.3965*temp*Math.pow(windKmh, 0.16);
		
		return Math.round(feelslike*10) / 10.0;
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  기상청 단기예보 API 호출후 반환
	 */
	@Cacheable(value = "weatherHour", key = "#nx + ':' + #ny", unless = "#result == null || #result.isEmpty()")
	@Override
	public List<WeatherHour> getHourWeather(int nx, int ny) {
	    BaseDateTime base = getVilageBaseDateTime();
	    String response = weatherClient.getVilageFcst(serviceKey, 1, 1000, "JSON", base.getBaseDate(), base.getBaseTime(), nx, ny);
	    
	    List<WeatherHourApi> items = parseVilageItems(response);
	    if (items.isEmpty()) return List.of();

	    LocalDateTime now = LocalDateTime.now();
	    LocalDateTime hourBase = now.withMinute(0).withSecond(0).withNano(0);

	    Map<String, List<WeatherHourApi>> timeGroup = items.stream()
	        .collect(Collectors.groupingBy(item -> item.getFcstDate() + item.getFcstTime()));

	    return timeGroup.entrySet().stream()
	    		.sorted(Map.Entry.comparingByKey()) 
	    		.map(entry -> {	
	    			String fcstTime = entry.getKey().substring(8);
	    			LocalDateTime fcstDateTime = LocalDateTime.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
	    			
	            return Map.entry(fcstDateTime, toWeatherHour(fcstTime, entry.getValue()));
	        })
	        .filter(entry -> !entry.getKey().isBefore(hourBase))
	        .limit(12) 
	        .map(Map.Entry::getValue)
	        .collect(Collectors.toList());
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  시간 계산 과 예외처리
	 */
	private BaseDateTime getVilageBaseDateTime() {

	    LocalDateTime now = LocalDateTime.now();

	    int hour = now.getHour();
	    int baseHour;

	    if (hour < 2) baseHour = 23;
	    else if (hour < 5) baseHour = 2;
	    else if (hour < 8) baseHour = 5;
	    else if (hour < 11) baseHour = 8;
	    else if (hour < 14) baseHour = 11;
	    else if (hour < 17) baseHour = 14;
	    else if (hour < 20) baseHour = 17;
	    else if (hour < 23) baseHour = 20;
	    else baseHour = 23;

	    LocalDateTime baseTime = now.withHour(baseHour).withMinute(0);

	    if (baseHour == 23 && hour < 2) {
	        baseTime = baseTime.minusDays(1);
	    }

	    String baseDate = baseTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	    String baseTimeStr = String.format("%02d00", baseHour);

	    return new BaseDateTime(baseDate, baseTimeStr);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  API DTO로 반환
	 */
	private List<WeatherHourApi> parseVilageItems(String response) {

	    try {
	        ObjectMapper mapper = new ObjectMapper();

	        JsonNode root = mapper.readTree(response);
	        JsonNode itemsNode = root
	                .path("response")
	                .path("body")
	                .path("items")
	                .path("item");

	        if (!itemsNode.isArray()) {
	            return List.of();
	        }

	        return mapper.convertValue(
	            itemsNode,
	            mapper.getTypeFactory()
	                  .constructCollectionType(List.class, WeatherHourApi.class)
	        );

	    } catch (Exception e) {
	        log.error("단기예보 파싱 실패", e);
	        return List.of();
	    }
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  시간대 예보들을 합성
	 */
	private WeatherHour toWeatherHour(String fcstTime, List<WeatherHourApi> list) {

	    Map<String, String> valueMap =
	        list.stream()
	            .collect(Collectors.toMap(
	                WeatherHourApi::getCategory,
	                WeatherHourApi::getFcstValue,
	                (a, b) -> a
	            ));

	    return WeatherHour.builder()
	        .time(formatTime(fcstTime))
	        .temp(parseInt(valueMap.get("TMP")))
	        .rainProb(parseInt(valueMap.get("POP")))
	        .icon(resolveHourIcon(valueMap))
	        .build();
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  상황에 맞는 날씨 아이콘 설정
	 */
	private String resolveHourIcon(Map<String, String> map) {

	    String pty = map.get("PTY");
	    String sky = map.get("SKY");

	    if (pty != null && !"0".equals(pty)) {
	        if ("1".equals(pty) || "2".equals(pty)) return "rain";
	        if ("3".equals(pty)) return "snow";
	        if ("4".equals(pty)) return "shower";
	    }

	    if ("1".equals(sky)) return "sun";
	    if ("3".equals(sky)) return "cloud";
	    if ("4".equals(sky)) return "overcast";

	    return "unknown";
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  현재날씨 새로고침
	 */
	@CacheEvict(value = "weatherMain", key = "#nx + ':' + #ny")
	@Override
	public Weather mainWeatherRefresh(int nx, int ny, double lat, double lon) {
		return mainWeatherDto(nx, ny, lat, lon);
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  주간날씨 
	 */
	@Cacheable(value = "weatherWeek", key = "#nx + ':' + #ny", unless = "#result == null || #result.isEmpty()")
	@Override
	public List<WeekWeather> getWeekWeather(int nx, int ny, double lat, double lon) {

	    List<WeekWeather> shortList =
	        buildWeekFromShortForecast(nx, ny);

	    String tmFc = getMidTmFc();
	    String landRegId = resolveLandRegId(lat, lon);
	    String taRegId   = resolveTaRegId(lat, lon);

	    String landJson = callMidLandFcst(landRegId, tmFc);
	    String taJson   = callMidTa(taRegId, tmFc);

	    List<WeekWeather> midList =
	        mergeWeekWeather(landJson, taJson);

	    List<WeekWeather> result = new ArrayList<>();
	    result.addAll(shortList);
	    result.addAll(midList);

	    return result;
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  tmFc 시간대 계산 
	 */
	private String getMidTmFc() {
	    LocalDateTime now = LocalDateTime.now();

	    LocalDateTime tmFc;
	    if (now.getHour() < 6) {
	        tmFc = now.minusDays(1).withHour(18);
	    } else if (now.getHour() < 18) {
	        tmFc = now.withHour(6);
	    } else {
	        tmFc = now.withHour(18);
	    }

	    tmFc = tmFc.withMinute(0).withSecond(0).withNano(0);

	    return tmFc.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  지역코드 구분
	 */
	private String resolveLandRegId(double lat, double lon) {

	    if (lat >= 37.0 && lat <= 38.5 && lon >= 126.0 && lon <= 128.0) {
	        return "11B00000";
	    }

	    if (lat >= 37.0 && lon >= 128.0) {
	        return "11D10000";
	    }

	    if (lat >= 36.0 && lat < 37.0) {
	        return "11C20000";
	    }

	    if (lat >= 34.5 && lat < 36.0) {
	        return "11F20000";
	    }

	    if (lat >= 35.0 && lon >= 128.0) {
	        return "11H20000";
	    }

	    if (lat < 34.5) {
	        return "11G00000";
	    }

	    return "11B00000";
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  기온예보 전용 지역코드
	 */
	private String resolveTaRegId(double lat, double lon) {

	    if (lat >= 37.4 && lat <= 37.7 && lon >= 126.8 && lon <= 127.2) {
	        return "11B10101";
	    }

	    if (lat >= 37.3 && lon < 126.8) {
	        return "11B20201";
	    }

	    if (lat >= 37.0 && lat < 37.4) {
	        return "11B20601";
	    }

	    if (lat >= 37.0 && lon >= 128.0) {
	        return "11D10301";
	    }

	    if (lat >= 36.0 && lat < 37.0) {
	        return "11C20401";
	    }

	    if (lat >= 34.5 && lat < 36.0) {
	        return "11F20501";
	    }

	    if (lat >= 35.0 && lon >= 128.0) {
	        return "11H20201";
	    }

	    if (lat < 34.5) {
	        return "11G00201";
	    }

	    return "11B10101";
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  중기 육상예보
	 */
	private String callMidLandFcst(String regId, String tmFc) {
	    return weekWeatherClient.getMidLandFcst(
	    	weekServiceKey,
	        1,
	        10,
	        "JSON",
	        regId,
	        tmFc);
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  중기 기온예보
	 */
	private String callMidTa(String regId, String tmFc) {
	    return weekWeatherClient.getMidTa(
	        weekServiceKey,
	        1,
	        10,
	        "JSON",
	        regId,
	        tmFc);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  실제 계산로직
	 */
	private List<WeekWeather> mergeWeekWeather(String landJson, String taJson) {

	    ObjectMapper jsonMapper = new ObjectMapper();
	    XmlMapper xmlMapper = new XmlMapper();

	    Map<Integer, WeekWeather.WeekWeatherBuilder> map = new HashMap<>();

	    try {
	        // ---------- 육상예보 ----------
	        JsonNode landItems = landJson.trim().startsWith("<")
	            ? xmlMapper.readTree(landJson).path("body").path("items").path("item")
	            : jsonMapper.readTree(landJson).path("response").path("body").path("items").path("item");

	        if (landItems.isArray()) landItems = landItems.get(0);

	        for (int day = 4; day <= 10; day++) {
	            WeekWeather.WeekWeatherBuilder builder =
	                map.computeIfAbsent(day, d -> WeekWeather.builder());

	            String wf = (day <= 7)
	                ? landItems.path("wf" + day + "Am").asText()
	                : landItems.path("wf" + day).asText();

	            int rn = (day <= 7)
	                ? landItems.path("rnSt" + day + "Am").asInt()
	                : landItems.path("rnSt" + day).asInt();

	            builder.condition(wf).rainProb(rn);
	        }

	        // ---------- 기온예보 ----------
	        JsonNode taItems = taJson.trim().startsWith("<")
	            ? xmlMapper.readTree(taJson).path("body").path("items").path("item")
	            : jsonMapper.readTree(taJson).path("response").path("body").path("items").path("item");

	        if (taItems.isArray()) taItems = taItems.get(0);

	        for (int day = 4; day <= 10; day++) {
	            WeekWeather.WeekWeatherBuilder builder = map.get(day);
	            if (builder == null) continue;

	            builder.minTemp(taItems.path("taMin" + day).asInt())
	                   .maxTemp(taItems.path("taMax" + day).asInt());
	        }

	        return map.entrySet().stream()
	            .sorted(Map.Entry.comparingByKey())
	            .map(e -> e.getValue()
	                .dayLabel(resolveDayLabelFromToday(e.getKey()))
	                .icon(resolveWeekIcon(e.getValue().build().getCondition()))
	                .build())
	            .collect(Collectors.toList());

	    } catch (Exception e) {
	        log.error("주간 날씨 병합 실패", e);
	        return List.of();
	    }
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  요일 라벨 계산
	 */
	private String resolveDayLabelFromToday(int diffDay) {
	    if (diffDay == 0) return "오늘";
	    if (diffDay == 1) return "내일";
	    if (diffDay == 2) return "모레";

	    return LocalDate.now()
	        .plusDays(diffDay)
	        .getDayOfWeek()
	        .getDisplayName(TextStyle.SHORT, Locale.KOREAN);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  아이콘 결정 계산
	 */
	private String resolveWeekIcon(String condition) {
	    if (condition.contains("비")) return "rain";
	    if (condition.contains("눈")) return "snow";
	    if (condition.contains("맑")) return "sun";
	    if (condition.contains("흐")) return "overcast";
	    return "cloud";
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  단기예보 -> 하루별로 변경
	 */
	private List<WeekWeather> buildWeekFromShortForecast(int nx, int ny) {

	    BaseDateTime base = getVilageBaseDateTime();
	    String response = weatherClient.getVilageFcst(
	        serviceKey, 1, 1000, "JSON",
	        base.getBaseDate(), base.getBaseTime(), nx, ny
	    );

	    List<WeatherHourApi> items = parseVilageItems(response);
	    if (items.isEmpty()) return List.of();

	    LocalDate today = LocalDate.now();

	    Map<LocalDate, List<WeatherHourApi>> dayMap =
	        items.stream().collect(Collectors.groupingBy(item ->
	            LocalDate.parse(item.getFcstDate(),
	                DateTimeFormatter.ofPattern("yyyyMMdd"))
	        ));

	    return dayMap.entrySet().stream()
	        .filter(e -> {
	            long diff = java.time.temporal.ChronoUnit.DAYS
	                .between(today, e.getKey());
	            return diff >= 0 && diff <= 3; // ✅ 3일까지
	        })
	        .sorted(Map.Entry.comparingByKey())
	        .map(e -> buildOneDayFromHours(today, e.getKey(), e.getValue()))
	        .collect(Collectors.toList());
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-26
	 *  단기예보 -> 하루 로직
	 */
	private WeekWeather buildOneDayFromHours(
	        LocalDate today,
	        LocalDate date,
	        List<WeatherHourApi> list) {

	    int minTemp = Integer.MAX_VALUE;
	    int maxTemp = Integer.MIN_VALUE;
	    int maxRain = 0;

	    Map<String, Long> skyCount = new HashMap<>();
	    boolean hasRainOrSnow = false;
	    String ptyResult = null;

	    for (WeatherHourApi item : list) {
	        String cat = item.getCategory();
	        String val = item.getFcstValue();

	        switch (cat) {
	            case "TMP" -> {
	                int t = Integer.parseInt(val);
	                minTemp = Math.min(minTemp, t);
	                maxTemp = Math.max(maxTemp, t);
	            }
	            case "POP" -> maxRain = Math.max(maxRain, Integer.parseInt(val));
	            case "PTY" -> {
	                if (!"0".equals(val)) {
	                    hasRainOrSnow = true;
	                    ptyResult = val;
	                }
	            }
	            case "SKY" -> skyCount.merge(val, 1L, Long::sum);
	        }
	    }

	    String condition;
	    if (hasRainOrSnow) {
	        condition = switch (ptyResult) {
	            case "1", "2" -> "비";
	            case "3" -> "눈";
	            case "4" -> "소나기";
	            default -> "비";
	        };
	    } else {
	        String sky = skyCount.entrySet().stream()
	            .max(Map.Entry.comparingByValue())
	            .map(Map.Entry::getKey)
	            .orElse("1");

	        condition = switch (sky) {
	            case "1" -> "맑음";
	            case "3" -> "구름 많음";
	            case "4" -> "흐림";
	            default -> "구름 많음";
	        };
	    }

	    String label;
	    long diff = java.time.temporal.ChronoUnit.DAYS.between(today, date);
	    if (diff == 0) label = "오늘";
	    else if (diff == 1) label = "내일";
	    else label = "모레";

	    return WeekWeather.builder()
	        .dayLabel(label)
	        .condition(condition)
	        .icon(resolveWeekIcon(condition))
	        .minTemp(minTemp)
	        .maxTemp(maxTemp)
	        .rainProb(maxRain)
	        .build();
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-01-27
	 *  강수량,적살량 에러방지코드
	 */
	private Double parseDoubleSafe(String value) {
	    if (value == null || value.contains("없음")) return null;
	    return Double.parseDouble(value);
	}
	
	
}
