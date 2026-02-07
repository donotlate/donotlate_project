package com.late.donot.chart.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.late.donot.chart.model.service.ChartService;

@Controller
@RequestMapping("chart")
public class ChartController {
	
	@Autowired
	private ChartService chartService;

	/** 작성자 : 유건우
	 * 작성일자 : 2026-02-07
	 * 상단 차트 내용 로드
	 */
	@GetMapping("topChart")
	@ResponseBody
	public Map<String, Object> topChart() {
		return chartService.getTopChart();
	}
}
