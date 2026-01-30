package com.late.donot.mypage.model.service;

import java.util.Map;

public interface MyPageService {

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-29
     * 마이페이지 - 이름변경
     */
    boolean nameChange(String changedName, int memberNo);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-29
     * 마이페이지 - 비밀번호 변경
     */
    boolean changePw(Map<String, Object> data, int memberNo);

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-30
     * 마이페이지 - 회원탈퇴
     */
    boolean deleteMember(int memberNo, String deletePW);

}
