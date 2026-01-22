package com.late.donot.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
}
