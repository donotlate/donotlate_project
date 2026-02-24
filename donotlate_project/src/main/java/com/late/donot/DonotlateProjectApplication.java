package com.late.donot;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

import jakarta.annotation.PostConstruct;

@EnableCaching
@EnableFeignClients
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class DonotlateProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonotlateProjectApplication.class, args);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-24
	 *  JVM 시간대 한국시간대로 조정
	 */
	@PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
