package com.late.donot.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.late.donot.member.model.dto.KakaoUserInfoResponseDto;
import com.late.donot.member.model.dto.Member;
import com.late.donot.member.model.service.KakaoService;
import com.late.donot.member.model.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("member")
@Slf4j
public class MemberController {

    @Autowired
    private MemberService service;

    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;
    
    @Autowired
    private KakaoService KakaoService;

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

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 아이디 중복 체크
     */
    @PostMapping("checkId")
    @ResponseBody
    public int checkId(@RequestBody String memberEmail) {
        String cleanEmail = memberEmail.replaceAll("\"", "");
        System.out.println("입력받은 이메일: " + cleanEmail);
        return service.checkId(cleanEmail);
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 회원가입 메일 발송
     */
    @PostMapping("sendAuthKey")
    @ResponseBody
    public int sendAuthKey(@RequestBody Map<String, String> map) {
        String email = map.get("email");
		String authKey = service.sendEmail("signUp", email);

        if(authKey != null) {
		    return 1;
		}

		return 0;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 인증번호 확인
     */
    @PostMapping("checkAuthKey")
    @ResponseBody
    public int checkAuthKey(@RequestBody Map<String, String> map) {
        return service.checkAuthKey(map);
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 회원가입
     */
    @PostMapping("signup")
    public String signup(Member inputMember, RedirectAttributes ra) {
        int result = service.signup(inputMember);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			message = inputMember.getMemberName() + "님 환영합니다!";
			path =  "/?login=true";
			
		}else {
			message = "회원가입에 실패하였습니다.";
			path = "signup";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 비밀번호 초기화 페이지 호출
     */
    @GetMapping("resetPw")
    public String resetPw() {
        return "resetPw";
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 비밀번호 초기화
     */
    @PostMapping("resetPw")
    @ResponseBody
    public int resetPw(@RequestBody Map<String, String> map) {
        String memberEmail = map.get("memberEmail");
        return service.resetPw(memberEmail);
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-26
     * 카카오 인증 API 호출
     */
    @GetMapping("kakaoLogin")
    public String kakaoLogin() {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirect_uri;

        return "redirect:"+location;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-26
     * 카카오 인증 API 호출 후 리다이렉트 메소드
     * 토큰 확인 -> 멤버 DTO에 대입 -> 로그인 처리
     */
    @GetMapping("login/oauth2/kakao") // yml에 등록한 redirect_uri의 경로
    public String callback(@RequestParam("code") String code, HttpServletRequest req, RedirectAttributes ra) {
        String accessToken = KakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = KakaoService.getUserInfo(accessToken);

        // 2. 로그인/회원가입 처리
        // memberService.kakaoLogin 내에서 DB 조회 후 없으면 INSERT, 있으면 로그인 처리
        Member loginMember = service.kakaoLogin(userInfo);

        if (loginMember != null) {
            // 3. 기존 일반 로그인과 동일하게 세션에 정보 저장
            req.getSession().setAttribute("loginMember", loginMember);
            return "redirect:/main";
        } else {
            ra.addFlashAttribute("message", "카카오 로그인 중 오류가 발생했습니다.");
            return "redirect:/";
        }
    }
}