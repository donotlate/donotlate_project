package com.late.donot.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionController {

	/**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 404, 500 에러 페이징 처리
     */
	@ExceptionHandler(NoResourceFoundException.class)
	public String noFound(){
		return "error/404";
	}

	//프로젝트에서 발생하는 모든 종류의 예외를 500으로 처리
	@ExceptionHandler(Exception.class)
	public String allExceptionHandler(Model model, Exception e){
		e.printStackTrace();
		model.addAttribute("exception", e);
		return "error/500";
	}
}
