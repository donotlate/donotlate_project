package com.late.donot.member.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.late.donot.member.model.dto.GoogleTokenResponseDTO;
import com.late.donot.member.model.dto.GoogleUserInfoResponseDTO;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class GoogleService {
    @Value("${google.client-id}")
    private String clientId;
    @Value("${google.client-secret}")
    private String clientSecret;
    @Value("${google.redirect-uri}")
    private String redirectUri;

    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_USER_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-27
     * 구글 액세스 토큰 받기 (DTO 활용)
     */
    public String getAccessTokenFromGoogle(String code) {
        GoogleTokenResponseDTO responseDto = WebClient.create()
                .post()
                .uri(GOOGLE_TOKEN_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue("code=" + code +
                           "&client_id=" + clientId +
                           "&client_secret=" + clientSecret +
                           "&redirect_uri=" + redirectUri +
                           "&grant_type=authorization_code")
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> 
                response.bodyToMono(String.class).flatMap(errorBody -> {
                    log.error("[Google Token Error] 상태코드: {}, 에러내용: {}", response.statusCode(), errorBody);
                    return Mono.error(new RuntimeException("구글 토큰 발급 중 오류 발생"));
                }))
                .bodyToMono(GoogleTokenResponseDTO.class)
                .block();

        return responseDto.getAccessToken();
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-27
     * 구글 사용자 정보 가져오기 (DTO 활용)
     */
    public GoogleUserInfoResponseDTO getUserInfo(String accessToken) {
        return WebClient.create()
                .get()
                .uri(GOOGLE_USER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> 
                response.bodyToMono(String.class).flatMap(errorBody -> {
                    log.error("[Google User Info Error] 상태코드: {}, 에러내용: {}", response.statusCode(), errorBody);
                    return Mono.error(new RuntimeException("구글 사용자 정보 조회 중 오류 발생"));
                }))
                .bodyToMono(GoogleUserInfoResponseDTO.class)
                .block();
    }
}
