package com.late.donot.member.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.late.donot.member.model.dto.KakaoTokenResponseDTO;
import com.late.donot.member.model.dto.KakaoUserInfoResponseDto;

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

    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponseDTO kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri("/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                // queryParam 대신 body에 담아 전송
                .bodyValue("grant_type=authorization_code" +
                           "&client_id=" + clientId +
                           "&redirect_uri=" + redirectUri + // 필수 추가
                           "&code=" + code)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> 
                    response.bodyToMono(String.class).flatMap(errorBody -> {
                        log.error("[Kakao Token Error] {}", errorBody);
                        return Mono.error(new RuntimeException("카카오 토큰 발급 실패"));
                    }))
                .bodyToMono(KakaoTokenResponseDTO.class)
                .block();

        return kakaoTokenResponseDto.getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        return WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> 
                    response.bodyToMono(String.class).flatMap(errorBody -> {
                        log.error("[Kakao User Error] {}", errorBody);
                        return Mono.error(new RuntimeException("카카오 사용자 정보 조회 실패"));
                    }))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }
}
