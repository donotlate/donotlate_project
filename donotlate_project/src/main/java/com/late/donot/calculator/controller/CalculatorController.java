package com.late.donot.calculator.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.late.donot.api.dto.DayType;
import com.late.donot.api.dto.Route;
import com.late.donot.calculator.model.dto.PushSaveRequest;
import com.late.donot.calculator.model.dto.RouteRequestDTO;
import com.late.donot.calculator.model.service.CalculatorService;
import com.late.donot.member.model.dto.Member;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("calculator")
@Slf4j
public class CalculatorController {
	
	@Autowired
	private CalculatorService calculatorService;
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-03
	 *  대중교통 루트 계산
	 */
	@GetMapping("/routes")
	public List<Route> routes(
	        @RequestParam("sx") double sx,
	        @RequestParam("sy") double sy,
	        @RequestParam("ex") double ex,
	        @RequestParam("ey") double ey,
	        @RequestParam("mode") String mode,
	        @RequestParam(name = "departureTime", required = false) LocalTime departureTime,
	        @RequestParam(name = "dayType", required = false) DayType dayType) {

	    return calculatorService.findRoute(
	            sx, sy, ex, ey, mode, departureTime, dayType);
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 *  출발시간과 요일까지 포함해 경로를 계산해 반환
	 */
	@PostMapping("/routes")
	public List<Route> routesWithTime(
	        @RequestBody RouteRequestDTO req
	) {
	    log.info("POST routes with time");

	    return calculatorService.findRoute(
	            req.getSx(),
	            req.getSy(),
	            req.getEx(),
	            req.getEy(),
	            req.getMode(),
	            req.getDepartureTime(),
	            req.getDayType()
	    );
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19
	 *  계산 push 저장
	 */
	@PostMapping("/push/save")
	public int savePush(@RequestBody PushSaveRequest request, HttpSession session) {

	    Member loginMember = (Member) session.getAttribute("loginMember");

	    if (loginMember == null) {
	        return 0;
	    }

	    request.setMemberNo(loginMember.getMemberNo());

	    return calculatorService.savePushWithRoute(request);
	}

}
