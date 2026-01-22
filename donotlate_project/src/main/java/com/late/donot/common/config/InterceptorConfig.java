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
	
	/** 작성자 : 유건우
	 *  작성일 : 2026-01-22
	 *  인증하지 않을 URL 허용
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(loginCheckInterceptor)
		.addPathPatterns("/**")
		.excludePathPatterns("/", "/signUp", "/error/**", "/member/**", "/;jsessionid=**",
                            "/css/**", "/js/**", "/favicon.ico");		
	}
}