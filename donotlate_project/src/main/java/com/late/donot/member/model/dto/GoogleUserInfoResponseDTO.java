package com.late.donot.member.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfoResponseDTO {
    @JsonProperty("id")
    private String id;      // 구글 고유 ID
    @JsonProperty("email")
    private String email;
    @JsonProperty("name")
    private String name;    // 전체 이름
    @JsonProperty("picture")
    private String picture; // 프로필 이미지
}