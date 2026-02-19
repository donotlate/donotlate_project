package com.late.donot.common.interceptor;

import java.io.PrintWriter;

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

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) { // 통로 느낌
	        return true;
	    }
		
        HttpSession session = request.getSession();
        String uri = request.getRequestURI();
        

        Object loginMember = session.getAttribute("loginMember");


        if (uri.startsWith("/admin")) { // 로그인 확인 부분
            if (loginMember == null) {
                
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); 

                
                return false; 
            }
            return true;
        }

        if (loginMember != null) {
            return true;
        }

        response.sendRedirect("/?login=true&loginError=1");
        return false;
    }
}
