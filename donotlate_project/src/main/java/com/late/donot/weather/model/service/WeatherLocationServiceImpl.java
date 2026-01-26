package com.late.donot.weather.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.late.donot.weather.client.KakaoClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WeatherLocationServiceImpl implements WeatherLocationService{
	
	@Autowired
	private KakaoClient kakaoClient;
	
	@Value("${kakao.api.key}")
	private String kakaoApiKey;

	/** 작성자 : 이승준
	 *  작성일 : 2026-01-22
	 *  카카오 api로 현재 위치 주소명 
	 */
	@Override
	public String getLocationName(double lat, double lon) {
		
		String response = kakaoClient.coordToAddress(
							"KakaoAK " + kakaoApiKey,
							lon,
							lat);
		
		try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            JsonNode document = root
                .path("documents")
                .get(0);

            JsonNode road = document.path("road_address");
            if (!road.isMissingNode() && !road.isNull()) {
                return road.path("address_name").asText();
            }

            return document
                .path("address")
                .path("address_name")
                .asText();

        } catch (Exception e) {
        	log.error("카카오 주소 변환 실패", e);
            return "알 수 없는 위치";
        }
		
	}	

}
