package com.late.donot.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private int memberNo;
	private String memberEmail;
	private String memberName;
	private String memberPw;
	private String memberDelFl;
	private int authority;
	private String profileImg;
	private String enrollDate;
	private String updateDate;
    private String socialType;
    private String socialId;
}
