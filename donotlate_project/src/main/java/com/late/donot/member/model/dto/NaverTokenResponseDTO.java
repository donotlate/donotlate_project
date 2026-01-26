package com.late.donot.member.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * 작성자 : 유건우
 * 작성일 : 2026-01-26
 * 네이버 API 토큰 응답값 DTO
 */
public class NaverTokenResponseDTO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private String expiresIn;

    // 에러 발생 시 담기는 필드들
    @JsonProperty("error")
    private String error;

    @JsonProperty("error_description")
    private String errorDescription;
}