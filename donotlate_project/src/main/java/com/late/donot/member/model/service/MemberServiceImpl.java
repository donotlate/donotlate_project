package com.late.donot.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.member.model.dto.Member;
import com.late.donot.member.model.mapper.MemberMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper mapper;

    @Autowired
	private BCryptPasswordEncoder encoder;
    
    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 로그인 서비스
     */
    @Override
    public Member login(Member inputMember) {
        Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		if(loginMember == null) {
			return null;
		}
		
		if(!encoder.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {		
			return null;
		}
		
		loginMember.setMemberPw(null);
		
		return loginMember;
    }
    
}
