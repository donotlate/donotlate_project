package com.late.donot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@EnableFeignClients
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class DonotlateProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonotlateProjectApplication.class, args);
	}

}
