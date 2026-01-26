package com.late.donot.admin.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.admin.model.mapper.AdminMapper;
import com.late.donot.member.model.dto.Member;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminServiceIpml implements AdminService{

	@Autowired
	public AdminMapper mapper;
	
	@Autowired
	private  BCryptPasswordEncoder bcrypt;
	
	// 로그인 
	@Override
	public Member login(Member inputMember) {
		
		Member loginMember = mapper.login(inputMember);
		
		if(loginMember == null) return null;
		
		if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) return null;

		loginMember.setMemberPw(null);
		return loginMember;
	}
	
	

}
