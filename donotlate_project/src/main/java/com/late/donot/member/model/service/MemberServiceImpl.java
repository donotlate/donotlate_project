package com.late.donot.member.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.late.donot.member.model.dto.Member;
import com.late.donot.member.model.mapper.MemberMapper;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper mapper;

    @Autowired
	private BCryptPasswordEncoder encoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailHandler mailHandler;

    @Autowired
    private SpringTemplateEngine templateEngine;
    
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

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 아이디 중복 체크
     */
    @Override
    public int checkId(String memberEmail) {
        return mapper.checkId(memberEmail);
    }

    @Override
    public String sendEmail(String type, String email) {
        String authKey = createAuthKey();
        
        Map<String, String> map = new HashMap<>();
        map.put("authKey", authKey);
        map.put("email", email);
        
        // 1. DB에 인증번호 저장 (이 작업은 동기적으로 처리되어야 함)
        if(!storeAuthKey(map)) return null;
        
        // 2. 메일 객체 생성
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("늦지마 회원가입 인증번호 메일입니다.");
            helper.setText(loadhtml(authKey, type), true);
            helper.addInline("logo", new ClassPathResource("static/images/logo.jpg"));
            
            // 3. 비동기로 메일 발송 호출 (기다리지 않고 바로 다음 줄 실행!)
            mailHandler.sendMail(mimeMessage);
            
            // 4. 즉시 인증번호 반환 (사용자는 0.1초 만에 응답을 받음)
            return authKey; 
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;        
        }
    }

    /** 작성자 : 유건우
	 * 작성일 : 2026-01-21
	 * HTML 문자열 완성기능
	 */
	private String loadhtml(String authKey, String type) {

		Context context = new Context();
		context.setVariable("authKey", authKey);
		
		return templateEngine.process("email/" + type, context);
	}

    /** 작성자 : 유건우
	 * 작성일 : 2026-01-21
	 *  DB에 인증키와 이메일 저장
	 */
    private boolean storeAuthKey(Map<String,String> map) {
        int result = mapper.selectAuthKey(map);
		
		if(result == 0) {
			result = mapper.insertAuthKey(map);
		}
        else {
            result = mapper.updateAuthKey(map);
        }
		
		return result > 0;
    }

    /** 작성자 : 유건우
	 * 작성일 : 2026-01-21
	 * 랜덤 6자 인증번호 발급 기능
	 */
    private String createAuthKey() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 인증번호 확인
     */
    @Override
    public int checkAuthKey(Map<String, String> map) {
        int result = mapper.checkAuthKey(map);
        
        if (result > 0) {
            mapper.deleteAuthKey(map.get("email"));
            return 1;
        }
        
        return 0;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 회원가입
     */
    @Override
    public int signup(Member inputMember) {
        String encPw = encoder.encode(inputMember.getMemberPw());
		inputMember.setMemberPw(encPw);
		
		return mapper.signup(inputMember);
    }
    
}
