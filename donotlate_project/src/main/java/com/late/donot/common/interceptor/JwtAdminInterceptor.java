package com.late.donot.common.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.late.donot.common.config.JwtUtilConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtilConfig jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // CORS preflight(OPTIONS)는 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String auth = request.getHeader("Authorization");

        // 토큰 없음 / 형식 틀림
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("UNAUTHORIZED");
            return false;
        }

        String token = auth.substring(7);

        // 토큰 검증 실패
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("INVALID_TOKEN");
            return false;
        }

        // 권한 체크 (ADMIN만 허용)
        String role = jwtUtil.getRoleFromToken(token);
        if (!"ADMIN".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("FORBIDDEN");
            return false;
        }

        // 컨트롤러에서 필요하면 email 꺼내 쓰게 저장
        String email = jwtUtil.getEmailFromToken(token);
        request.setAttribute("authEmail", email);
        
        // 번호 꺼내 쓰게 저장
        Integer memberNo = jwtUtil.getMemberNoFromToken(token);
        request.setAttribute("authMemberNo", memberNo);

        return true;
    }
}
