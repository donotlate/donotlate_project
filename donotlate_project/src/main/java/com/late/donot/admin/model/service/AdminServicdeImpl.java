package com.late.donot.admin.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.admin.mapper.AdminMapper;
import com.late.donot.member.model.dto.Member;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AdminServicdeImpl implements AdminService {
	
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Autowired
	private AdminMapper mapper; 
	
	// 로그인
	@Override
	public Member login(Member inputMember) {

		
	Member loginMember = mapper.login(inputMember.getMemberEmail());
		
	if(loginMember == null) return null;
		
		if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) return null;
		
		loginMember.setMemberPw(null);
		return loginMember;
		

	}

}
