package com.late.donot.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.late.donot.member.model.dto.Member;
import com.late.donot.member.model.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("member")
public class MemberController {

    @Autowired
    private MemberService service;

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 로그인 기능
     */
    @PostMapping("login")
	public String login(Member inputMember, RedirectAttributes ra, HttpServletResponse resp, HttpServletRequest req) {
		
		Member loginMember = service.login(inputMember);
		
		if(loginMember == null) {
			ra.addFlashAttribute("message", "로그인 실패, 아이디 또는 비밀번호가 일치하지 않습니다");		
			return "redirect:/";
		}
        else {
			req.getSession().setAttribute("loginMember", loginMember);
		}
		
		return "redirect:/main";
	}

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 로그아웃 기능
     */
    @GetMapping("logout")
    public String logout(HttpServletResponse resp, HttpServletRequest req) {
        req.getSession().invalidate();
        return "redirect:/";
    }
}
