package com.late.donot.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.late.donot.admin.model.service.AdminService;
import com.late.donot.member.model.dto.Member;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "http://192.168.32.19:5173")
@RequestMapping("admin")
@SessionAttributes({ "loginMember" })
public class AdminController {

	@Autowired
	private AdminService service;

	/**
	 * 작성자 양충모 작성일 2026-01-26 로그인
	 * 
	 * @param inputMember
	 * @param session
	 * @return
	 */
	@PostMapping("login")
	public Member login(@RequestBody Member inputMember, HttpSession session) {

		Member loginMember = service.login(inputMember);

		if (loginMember == null)
			return null;

		session.setAttribute("loginMember", loginMember);

		System.out.println("관지자 정보" + loginMember);
		return loginMember;

	}

	/**
	 * 작성자: 양충모 작성일: 01-28 로그아웃
	 * 
	 * @param sessionStatus
	 * @return
	 */
	@GetMapping("logout")
	public String logout(HttpSession session) {

		session.invalidate();

		return "redirect:/";

	}
	
	/** 작성자: 양충모 
	 * 	작성일: 01-29
	 *  유저 조회
	 * @param member
	 * @return
	 */
	@GetMapping("Users")
	public List<Member> getUsers() {
		
		List<Member> userList = service.getUsers();
		
		return userList;
		
	}
	
	/** 작성자: 양충모
	 *  작성일: 01-30
	 *  유저 수정
	 * @param member
	 * @return
	 */
	@PutMapping("editUser")
	public int editUser(@RequestBody Member member) {
		
		int setuser = service.editUser(member);
		
		return setuser;
		
	}
	
	/** 작성자: 양충모
	 *  작성일: 02-02
	 *  유저 삭제
	 * @param member
	 * @return
	 */
	@DeleteMapping("removeUser")
	public List<Member> removeUser(@RequestParam("memberNo")  int memberNo) {
	    return service.removeUser(memberNo);
	}
	
	/** 작성자: 양충모
	 *  작성일: 02-03
	 *  유저 추가
	 * @param member
	 * @return
	 */
	@PostMapping("createUser")
	public List<Member> createUser(@RequestBody Member inputMember) {
		return service.createUser(inputMember);
	}
	
	
	
}
