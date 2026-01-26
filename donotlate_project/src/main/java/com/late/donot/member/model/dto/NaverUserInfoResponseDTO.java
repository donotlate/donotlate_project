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
 * 네이버 API 사용자 정보 응답값 DTO
 */
public class NaverUserInfoResponseDTO {

    @JsonProperty("resultcode")
    private String resultCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("response")
    private Response response;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        // 네이버 고유 식별자 (문자열)
        @JsonProperty("id")
        private String id;

        // 이메일
        @JsonProperty("email")
        private String email;

        // 별명
        @JsonProperty("nickname")
        private String nickname;

        // 이름
        @JsonProperty("name")
        private String name;

        // 프로필 사진 URL
        @JsonProperty("profile_image")
        private String profileImage;

        // 성별 (F: 여성, M: 남성, U: 확인불가)
        @JsonProperty("gender")
        private String gender;

        // 연령대
        @JsonProperty("age")
        private String age;

        // 생일 (MM-DD)
        @JsonProperty("birthday")
        private String birthday;

        // 출생연도 (YYYY)
        @JsonProperty("birthyear")
        private String birthyear;

        // 휴대전화번호
        @JsonProperty("mobile")
        private String mobile;
    }
}