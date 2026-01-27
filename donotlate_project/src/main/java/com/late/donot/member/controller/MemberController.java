package com.late.donot.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.late.donot.member.model.dto.GoogleUserInfoResponseDTO;
import com.late.donot.member.model.dto.KakaoUserInfoResponseDTO;
import com.late.donot.member.model.dto.Member;
import com.late.donot.member.model.dto.NaverUserInfoResponseDTO;
import com.late.donot.member.model.service.GoogleService;
import com.late.donot.member.model.service.KakaoService;
import com.late.donot.member.model.service.MemberService;
import com.late.donot.member.model.service.NaverService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("member")
public class MemberController {

    @Autowired
    private MemberService service;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;
    
    @Autowired
    private KakaoService KakaoService;
    
    @Value("${naver.client-id}")
    private String naverClientId;
    
    @Value("${naver.redirect-uri}")
    private String naverRedirectUri;
       
    @Autowired
    private NaverService naverService;

    @Value("${google.client-id}")
	private String googleClientId;

	@Value("${google.redirect-uri}")
	private String googleRedirectUri;
	
	@Autowired
	private GoogleService googleService;
    
    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 로그인 기능
     */
    @PostMapping("login")
	public String login(Member inputMember, RedirectAttributes ra, HttpServletResponse resp, HttpServletRequest req) {
		
		Member loginMember = service.login(inputMember);
		
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		
		
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
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&kakaoClientId="+kakaoClientId+"&kakaoRedirectUri="+kakaoRedirectUri;

        return "redirect:"+location;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-26
     * 카카오 인증 API 호출 후 리다이렉트 메소드
     * 토큰 확인 -> 멤버 DTO에 대입 -> 로그인 처리
     */
    @GetMapping("login/oauth2/kakao") // yml에 등록한 kakaoRedirectUri의 경로
    public String kakaoCallback(@RequestParam("code") String code, HttpServletRequest req, RedirectAttributes ra) {
        // 1. 토큰 획득
    	String accessToken = KakaoService.getAccessTokenFromKakao(code);
    	// 2. 유저 정보 획득
        KakaoUserInfoResponseDTO userInfo = KakaoService.getUserInfo(accessToken);

        // 3. 로그인/회원가입 처리
        // memberService.kakaoLogin 내에서 DB 조회 후 없으면 INSERT, 있으면 로그인 처리
        Member loginMember = service.kakaoLogin(userInfo);

        if (loginMember != null) {
            req.getSession().setAttribute("loginMember", loginMember);
            return "redirect:/main";
        } else {
            ra.addFlashAttribute("message", "이미 동일한 이메일로 가입된 계정이 존재합니다. 일반 로그인을 이용해 주세요.");
            return "redirect:/";
        }
    }
    
    
    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-26
     * 네이버 인증 API 호출
     */
    @GetMapping("naverLogin")
    public String naverLogin() {
        // state: 사이트 간 요청 위조(CSRF) 공격 방지를 위한 임의의 상태 토큰 (보통 난수 생성 권장)
        // 여기서는 테스트를 위해 고정 문자열을 사용하거나 UUID를 활용할 수 있습니다. (추후 수정 필요)
        String state = "naver_login_state_test"; 
        
        String location = "https://nid.naver.com/oauth2.0/authorize"
                + "?response_type=code"
                + "&client_id=" + naverClientId
                + "&redirect_uri=" + naverRedirectUri
                + "&state=" + state;

        return "redirect:" + location;
    }
    
    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-26
     * 네이버 인증 API 호출 후 리다이렉트 메소드
     * 토큰 확인 -> 네이버 DTO 대입 -> 로그인 처리
     */
    @GetMapping("login/oauth2/naver")
    public String naverCallback(@RequestParam("code") String code, 
                                @RequestParam("state") String state, 
                                HttpServletRequest req, 
                                RedirectAttributes ra) {

        // 1. 네이버로부터 액세스 토큰 획득
        String accessToken = naverService.getAccessTokenFromNaver(code, state);

        // 2. 유저 정보 획득 (Map 대신 NaverUserInfoResponseDto 사용)
        NaverUserInfoResponseDTO userInfo = naverService.getUserInfo(accessToken);

        // 3. 로그인/회원가입 처리
        Member loginMember = service.naverLogin(userInfo);

        if (loginMember != null) {
            // 세션에 로그인 정보 저장
            req.getSession().setAttribute("loginMember", loginMember);
            return "redirect:/main";
        } else {
            // 에러 메시지 수정 (네이버 로그인으로 변경)
            ra.addFlashAttribute("message", "이미 동일한 이메일로 가입된 계정이 존재합니다. 일반 로그인을 이용해 주세요.");
            return "redirect:/";
        }
    }
    
    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-27
     * 구글 인증 API 호출
     */
    @GetMapping("googleLogin")
    public String googleLogin() {
        String location = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + googleClientId
                + "&redirect_uri=" + googleRedirectUri
                + "&response_type=code"
                + "&scope=email profile"; // 이메일과 프로필 정보를 가져오겠다는 권한 범위

        return "redirect:" + location;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-27
     * 구글 인증 API 호출 후 리다이렉트 메소드
     * 토큰 확인 -> 네이버 DTO 대입 -> 로그인 처리
     */
    @GetMapping("login/oauth2/google")
    public String googleCallback(@RequestParam("code") String code, 
                                 HttpServletRequest req, 
                                 RedirectAttributes ra) {
        
        // 1. 구글로부터 액세스 토큰 획득
        String accessToken = googleService.getAccessTokenFromGoogle(code);
        
        // 2. 유저 정보 획득 (DTO 활용)
        GoogleUserInfoResponseDTO userInfo = googleService.getUserInfo(accessToken);
        
        // 3. 로그인/회원가입 처리
        Member loginMember = service.googleLogin(userInfo);
        
        if (loginMember != null) {
            req.getSession().setAttribute("loginMember", loginMember);
            return "redirect:/main";
        } else {
            ra.addFlashAttribute("message", "이미 동일한 이메일로 가입된 계정이 존재합니다. 일반 로그인을 이용해 주세요.");
            return "redirect:/";
        }
    }
}