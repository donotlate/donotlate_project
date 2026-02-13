package com.late.donot.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.late.donot.common.interceptor.LoginCheckInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer{

    @Autowired
	private LoginCheckInterceptor loginCheckInterceptor;
    
    /** 작성자 : 양충모
     *	작성일: 2026-01-26
     *  cors 풀림
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
                .allowedOrigins("http://localhost:5173")
                .allowCredentials(true) // 쿠키 전송 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 명시적으로 허용
                .allowedHeaders("*");
    }
    
    
	
	/** 작성자 : 유건우
	 *  작성일 : 2026-01-22
	 *  인증하지 않을 URL 허용
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(loginCheckInterceptor)
		.addPathPatterns("/**")
		.excludePathPatterns("/", "/signUp", "/error/**", "/member/**", "/;jsessionid=**", "/admin/login",
                            "/css/**", "/js/**", "/favicon.ico");		
	}
	

}