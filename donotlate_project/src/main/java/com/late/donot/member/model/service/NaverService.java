package com.late.donot.member.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.late.donot.member.model.dto.NaverTokenResponseDTO;
import com.late.donot.member.model.dto.NaverUserInfoResponseDTO;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class NaverService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    private final String NAVER_TOKEN_URL_HOST = "https://nid.naver.com";
    private final String NAVER_USER_URL_HOST = "https://openapi.naver.com";

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-26
     * 네이버 액세스 토큰 받기 (DTO 활용)
     */
    public String getAccessTokenFromNaver(String code, String state) {
        NaverTokenResponseDTO responseDto = WebClient.create(NAVER_TOKEN_URL_HOST)
                .get() // 네이버는 토큰 발급 시 GET 권장
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth2.0/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code", code)
                        .queryParam("state", state)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> 
                response.bodyToMono(String.class).flatMap(errorBody -> {
                    log.error("[Naver Token Error] 상태코드: {}, 에러내용: {}", response.statusCode(), errorBody);
                    return Mono.error(new RuntimeException("네이버 토큰 발급 중 오류 발생"));
                }))
                .bodyToMono(NaverTokenResponseDTO.class)
                .block();

        return responseDto.getAccessToken();
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-26
     * 네이버 사용자 정보 가져오기 (DTO 활용)
     */
    public NaverUserInfoResponseDTO getUserInfo(String accessToken) {
        return WebClient.create(NAVER_USER_URL_HOST)
                .get()
                .uri("/v1/nid/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> 
                response.bodyToMono(String.class).flatMap(errorBody -> {
                    log.error("[Naver User Info Error] 상태코드: {}, 에러내용: {}", response.statusCode(), errorBody);
                    return Mono.error(new RuntimeException("네이버 사용자 정보 조회 중 오류 발생"));
                }))
                .bodyToMono(NaverUserInfoResponseDTO.class)
                .block();
    }
}