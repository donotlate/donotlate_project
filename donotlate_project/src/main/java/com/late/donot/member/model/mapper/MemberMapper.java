package com.late.donot.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.late.donot.member.model.dto.Member;

@Mapper
public interface MemberMapper {

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 로그인 서비스
     */
    Member login(String memberEmail);

}
