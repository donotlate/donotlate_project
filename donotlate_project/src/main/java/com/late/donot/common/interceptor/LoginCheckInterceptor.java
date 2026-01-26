package com.late.donot.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor{
	
    /** 작성자 : 유건우
	 *  작성일 : 2026-01-22
	 *  로그인 세션 검증
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		if(request.getSession().getAttribute("loginMember") != null) {
			return true;
		}

        response.sendRedirect("/?login=true&loginError=1");
		return false;
	}	
	
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//	    
//	    // 이 3줄이 핵심입니다!
//	    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//	        return true; // OPTIONS 요청은 검사하지 않고 통과시킴
//	    }
//
//	    // 기존 로그인 체크 로직...
//	    HttpSession session = request.getSession();
//	    if (session.getAttribute("loginMember") == null) {
//	        // ... 로그인 안 됐을 때 처리
//	        return false;
//	    }
//	    return true;
//	}
}
