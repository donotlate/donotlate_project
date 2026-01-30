package com.late.donot.mypage.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyPageMapper {

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-29
     * 마이페이지 - 이름변경
     */
    boolean nameChange(@Param("changedName") String changedName, @Param("memberNo") int memberNo);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-29
     * 마이페이지 - 비밀번호 변경 - 비밀번호 조회
     */
    String selectPw(int memberNo);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-29
     * 마이페이지 - 비밀번호 변경 - 비밀번호 수정
     */
    boolean changePw(Map<String,Object> data);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-30
     * 마이페이지 - 회원탈퇴
     */
    boolean deleteMember(@Param("memberNo") int memberNo);

}
