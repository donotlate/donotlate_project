package com.late.donot.calculator.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.late.donot.api.dto.DayType;
import com.late.donot.api.dto.Route;
import com.late.donot.calculator.model.service.CalculatorService;

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
	public List<Route> routes(@RequestParam("sx") double sx,
							  @RequestParam("sy") double sy,
							  @RequestParam("ex") double ex,
							  @RequestParam("ey") double ey,
							  @RequestParam("mode") String mode,
							  @RequestParam("departureTime") LocalTime departureTime,
							  @RequestParam("dayType") DayType dayType) {
		
		return calculatorService.findRoute(sx, sy, ex, ey, mode, departureTime, dayType);
	}

}
