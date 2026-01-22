package com.late.donot.member.model.service;

import java.util.Map;

import com.late.donot.member.model.dto.Member;

public interface MemberService {

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 로그인 서비스
     */
    Member login(Member inputMember);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 아이디 중복 체크
     */
    int checkId(String memberEmail);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 회원가입 메일 발송
     */
    String sendEmail(String string, String email);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 인증번호 확인
     */
    int checkAuthKey(Map<String,String> map);

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
    int resetPw(String memberEmail);
}
