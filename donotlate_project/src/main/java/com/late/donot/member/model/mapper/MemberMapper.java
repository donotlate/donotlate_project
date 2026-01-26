package com.late.donot.member.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.late.donot.member.model.dto.Member;

@Mapper
public interface MemberMapper {

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 로그인 서비스
     */
    Member login(String memberEmail);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 아이디 중복 체크
     */
    int checkId(String memberEmail);


    /** 작성자 : 유건우
	 * 작성일 : 2026-01-21
	 *  DB에 인증키와 이메일 조회, 저장
	 */
    int selectAuthKey(Map<String,String> map);

    int insertAuthKey(Map<String,String> map);

    int updateAuthKey(Map<String,String> map);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 인증번호 확인
     */
    int checkAuthKey(Map<String,String> map);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 이전 인증번호 삭제
     */
    void deleteAuthKey(String string);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 회원가입
     */
    int signup(Member inputMember);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 비밀번호 초기화
     */
    int resetPassword(Member resetPwMember);

    Member findMemberBySocial(@Param("socialId") String socialId, 
                            @Param("socialType") String socialType);

    void insertSocialMember(Member member);

}
