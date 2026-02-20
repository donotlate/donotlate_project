package com.late.donot.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;


@Configuration
@PropertySource("classpath:/config.properties")
public class FileConfig implements WebMvcConfigurer {

    // 파일 업로드 설정값
    @Value("${spring.servlet.multipart.file-size-threshold}")
    private long fileSizeThreshold;

    @Value("${spring.servlet.multipart.location}")
    private String location;

    @Value("${spring.servlet.multipart.max-request-size}")
    private long maxRequestSize;
    
    @Value("${spring.servlet.multipart.max-file-size}")
    private long maxFileSize;
    
    // 프로필 이미지 경로 설정
    @Value("${profile.resource-handler}")
    private String profileResourceHandler;
    
    @Value("${profile.resource-location}")
    private String profileResourceLocation;

    
    // 공지사항 이미지 경로 설정
    @Value("${notice.resource-handler}")
    private String noticeResourceHandler;

    @Value("${notice.resource-location}")
    private String noticeResourceLocation;
    
    @Bean
    public MultipartConfigElement configElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold)); //파일 업로드 임계값
		factory.setLocation(location); //임계값 초과시 파일의 임의 저장 경로
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize)); //요청당 파일 최대 크기
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize)); //개별 파일당 최대 크기
        return factory.createMultipartConfig();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 프로필 이미지 핸들러 등록
        registry.addResourceHandler(profileResourceHandler)
                .addResourceLocations(profileResourceLocation);
        

        // 공지 이미지
        registry.addResourceHandler(noticeResourceHandler)
                .addResourceLocations(noticeResourceLocation);
    }
    
    
    
    
}