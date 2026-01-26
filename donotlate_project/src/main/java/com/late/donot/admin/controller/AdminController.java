package com.late.donot.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.late.donot.admin.model.service.AdminService;
import com.late.donot.member.model.dto.Member;

import jakarta.servlet.http.HttpSession;



@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("admin")
@SessionAttributes({"loginMember"})
public class AdminController {

	@Autowired
	private AdminService service;
	
	@PostMapping("adminLogin")
	public Member login(@RequestBody Member inputMember , HttpSession session) {
		
		Member loginMember = service.login(inputMember);
		
		session.setAttribute("loginMember", loginMember);
		return loginMember;
		
	}
}
