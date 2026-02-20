package com.late.donot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	/** 작성자 : 유건우
	 *  작성일 : 2026-02-20
	 *  암호화 알고리즘 사용처리
	 */
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		
		return new BCryptPasswordEncoder();
		
	}
	
	
	/** 작성자 : 유건우
	 *  작성일 : 2026-02-20
	 *  인증하지 않을 URL 허용 및 SecurityConfig 사용처리
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http
	        .csrf(csrf -> csrf.disable())
	        .formLogin(form -> form.disable())
	        .httpBasic(basic -> basic.disable())
	        .authorizeHttpRequests(auth -> auth
	            // 인터셉터 excludePathPatterns에 해당하는 것들
	            .requestMatchers(
	                "/",
	                "/signUp",
	                "/error/**",
	                "/member/**",
	                "/admin/login",
	                "/css/**",
	                "/js/**",
	                "/favicon.ico"
	            ).permitAll()

	            // 그 외 모든 요청은 인증 필요
	            .anyRequest().authenticated()
	        )
		 
	        //인증 안 된 사용자가 접근 시
	        .exceptionHandling(exception -> exception
	            .authenticationEntryPoint((request, response, authException) -> {
	                response.sendRedirect("/?login=true&loginError=1");
	            })
	        );

	    return http.build();
	}
	
	/** 작성자 : 유건우
	 *  작성일 : 2026-02-20
	 * 개인 로그인만 사용하기 위해  기본 user 계정 사용안함처리
	 */
	@Bean
	public UserDetailsService userDetailsService() {
	    return new InMemoryUserDetailsManager();
	}
}
