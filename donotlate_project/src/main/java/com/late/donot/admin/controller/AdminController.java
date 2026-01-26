package com.late.donot.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.late.donot.admin.model.service.AdminService;
import com.late.donot.member.model.dto.Member;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("admin")
public class AdminController {

	@Autowired
	private AdminService service;
	
	@PostMapping("login")
	public Member login(@RequestBody Member inputMember , HttpSession session) {
		
		Member loginMember  = service.login(inputMember);
		
		if(loginMember == null) return null;

		session.setAttribute("loginMember", loginMember);
		
		System.out.println("관지자 정보" +loginMember);
		return loginMember;

	}
}
