package com.late.donot.member.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.late.donot.member.model.dto.KakaoTokenResponseDTO;
import com.late.donot.member.model.dto.KakaoUserInfoResponseDTO;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KakaoService {
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;
    private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    public KakaoService(@Value("${kakao.client-id}") String clientId, @Value("${kakao.redirect-uri}") String redirectUri) {
        this.clientId = clientId;
		this.redirectUri = redirectUri;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-26
     * 카카오 액세스 토큰 받기 (DTO 활용)
     */
    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponseDTO kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri("/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .bodyValue("grant_type=authorization_code" +
                           "&client_id=" + clientId +
                           "&redirect_uri=" + redirectUri + // 필수 추가
                           "&code=" + code)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> 
                response.bodyToMono(String.class).flatMap(errorBody -> {
                    log.error("[Kakao Token Error] 상태코드: {}, 에러내용: {}", response.statusCode(), errorBody);
                    return Mono.error(new RuntimeException("카카오 토큰 발급 중 오류 발생"));
                }))
                .bodyToMono(KakaoTokenResponseDTO.class)
                .block();

        return kakaoTokenResponseDto.getAccessToken();
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-26
     * 카카오 사용자 정보 가져오기 (DTO 활용)
     */
    public KakaoUserInfoResponseDTO getUserInfo(String accessToken) {
        return WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> 
                response.bodyToMono(String.class).flatMap(errorBody -> {
                    log.error("[Kakao User Info Error] 상태코드: {}, 에러내용: {}", response.statusCode(), errorBody);
                    return Mono.error(new RuntimeException("카카오 사용자 정보 조회 중 오류 발생"));
                }))
                .bodyToMono(KakaoUserInfoResponseDTO.class)
                .block();
    }
}
