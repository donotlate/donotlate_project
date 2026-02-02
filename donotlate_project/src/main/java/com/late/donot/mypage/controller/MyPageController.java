package com.late.donot.mypage.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.late.donot.member.model.dto.Member;
import com.late.donot.mypage.model.service.MyPageService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("myPage")
public class MyPageController {

    @Autowired
    private MyPageService service;

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-29
     * 마이페이지 - 이름변경
     */
    @GetMapping("nameChange")
    @ResponseBody
    public Map<String, Object> nameChange(@RequestParam("changedName") String changedName, HttpSession session){
        Map<String, Object> result = new HashMap<>();
        Member loginMember = getLoginMember(session, result);

        if(service.nameChange(changedName, loginMember.getMemberNo())){
            loginMember.setMemberName(changedName);
            result.put("status", "success");
            result.put("message", "이름이 성공적으로 변경되었습니다.");
        } else {
            result.put("status", "fail");
            result.put("message", "데이터베이스 업데이트에 실패했습니다.");
        }
        
        return result;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-29
     * 마이페이지 - 비밀번호 변경
     */
    @PostMapping("changePw")
    @ResponseBody
    public Map<String, Object> changePw(@RequestBody Map<String, Object> data,
                            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        Member loginMember = getLoginMember(session, result);

        if(service.changePw(data, loginMember.getMemberNo())){
            result.put("status", "success");
            result.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        } else {
            result.put("status", "fail");
            result.put("message", "현재 비밀번호가 일치하지 않습니다.");
        }

        return result;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-30
     * 마이페이지 - 회원탈퇴
     */
    @PostMapping("deleteMember")
    @ResponseBody
    public Map<String, Object> deleteMember(@RequestParam("deletePW") String deletePW, HttpSession session){
        Map<String, Object> result = new HashMap<>();
        Member loginMember = getLoginMember(session, result);

        if(service.deleteMember(loginMember.getMemberNo(), deletePW)){
            result.put("status", "success");
            result.put("message", "회원탈퇴가 성공적으로 완료되었습니다.\n 이용해주셔서 감사합니다.");
            session.invalidate();
        } else {
            result.put("status", "fail");
            result.put("message", "현재 비밀번호가 일치하지 않습니다.");
        }

        return result;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-30
     * 마이페이지 로직 수행전 세션 검증
     */
    private Member getLoginMember(HttpSession session, Map<String, Object> result) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            result.put("status", "fail");
            result.put("message", "세션이 만료되었습니다. 다시 로그인해주세요.");
        }

        return loginMember;
    }
}
