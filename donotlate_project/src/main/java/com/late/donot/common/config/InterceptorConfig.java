package com.late.donot.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.late.donot.common.interceptor.LoginCheckInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer{

    @Autowired
	private LoginCheckInterceptor loginCheckInterceptor;
    
	// 1. CORS 설정 추가: 프론트엔드(5173)의 접근을 허용합니다.
    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // 리액트 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
	
	/** 작성자 : 유건우
	 *  작성일 : 2026-01-22
	 *  인증하지 않을 URL 허용
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(loginCheckInterceptor)
		.addPathPatterns("/**")
		.excludePathPatterns("/", "/signUp", "/error/**", "/member/**", "/;jsessionid=**",
                            "/css/**", "/js/**", "/favicon.ico","/admin/adminLogin");		
	}
	

}