package com.late.donot.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.late.donot.common.interceptor.JwtAdminInterceptor;
import com.late.donot.common.interceptor.LoginCheckInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    @Autowired
    private JwtAdminInterceptor jwtAdminInterceptor; 


    /** 작성자 : 양충모
     *	작성일: 2026-01-26
     *  cors 풀림
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "https://donotlate-admin-project.vercel.app",
                        "https://donotlate.kro.kr"
                )
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

	/** 작성자 : 유건우
	 *  작성일 : 2026-01-22
	 *  인증하지 않을 URL 허용
	 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //  /admin/** 는 JWT로 보호
        registry.addInterceptor(jwtAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login"); // 로그인만 예외

        // 기존 세션 인터셉터는 /admin/** 제외(충돌 방지)
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/", "/signUp", "/error/**", "/member/**", "/;jsessionid=**",
                        "/css/**", "/js/**", "/favicon.ico",
                        "/admin/**"
                );
    }
}
