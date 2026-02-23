package com.late.donot.main.model.service;

import java.time.LocalDateTime;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.api.dto.CoordinatePoint;
import com.late.donot.api.dto.Weather;
import com.late.donot.weather.model.service.WeatherService;
import com.late.donot.weather.util.CoordinateConverter;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MainAiServiceImpl implements MainAiService{
	
	private final ChatClient chatClient;
	
	@Autowired
	private WeatherService weatherService;
	
	
	public MainAiServiceImpl(ChatClient.Builder builder) {
		this.chatClient = builder.build();
		}

	
	@Override
	public String generateDashboardComment(double lat, double lon) {

		CoordinatePoint point = CoordinateConverter.toCoordinate(lat, lon);

	    int nx = point.getNx();
	    int ny = point.getNy();

	    Weather weather = weatherService.mainWeatherDto(nx, ny, lat, lon);

	    String condition = weather.getCondition();

	    boolean rush = isRushHour(LocalDateTime.now());

	    int earlyMinutes = 5;
	    if (rush) earlyMinutes += 5;
	    if (condition.contains("비") || condition.contains("눈")) {
	        earlyMinutes += 5;
	    }

	    String prompt = """
	    당신은 통근 전략 코치입니다.

	    오늘의 날씨는 %s 입니다.
	    현재 교통 상황은 %s 입니다.

	    사용자의 기상 시간은 언급하지 마세요.
	    구체적인 시각은 제시하지 마세요.
	    "오늘의 날씨는 어떻고, 교통상황을 설명하고, 평소보다 %d분 일찍 출발하는 것을 추천합니다." 형식으로 작성하세요.
	    마지막은 긍정적인 덕담으로 마무리하세요.
	    """.formatted(
	            condition,
	            rush ? "출퇴근 혼잡 시간대" : "일반 시간대",
	            earlyMinutes
	    );

	    return chatClient.prompt()
	            .user(prompt)
	            .call()
	            .content()
	            .trim();
	}
	
	private boolean isRushHour(LocalDateTime dt) {
	    int hour = dt.getHour();
	    return (hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19);
	}
}
