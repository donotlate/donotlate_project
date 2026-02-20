package com.late.donot.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // preflight 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String uri = request.getRequestURI();

        //  관리자(/admin/**)는 JWT 인터셉터가 처리하므로 여기서는 무조건 통과
        if (uri.startsWith("/admin")) return true;

        //  세션 없으면 만들지 말고(null 가능)
        HttpSession session = request.getSession(false);
        Object loginMember = (session == null) ? null : session.getAttribute("loginMember");

        //  로그인 되어있으면 통과
        if (loginMember != null) return true;

        //  로그인 안 되어있으면 막기 (기존처럼 리다이렉트)
        response.sendRedirect("/?login=true&loginError=1");
        return false;
    }
}
