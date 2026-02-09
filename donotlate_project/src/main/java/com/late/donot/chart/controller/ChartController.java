package com.late.donot.chart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.late.donot.chart.model.service.ChartService;

@RestController
@RequestMapping("chart")
public class ChartController {
	
	@Autowired
	private ChartService chartService;

	/** 작성자 : 유건우
	 * 작성일자 : 2026-02-07
	 * 상단 차트 내용 로드
	 */
	@GetMapping("topChart")
	public Map<String, Object> topChart() {
		return chartService.getTopChart();
	}

	/** 작성자 : 유건우
	 * 작성일자 : 2026-02-07
	 * 지하철 요일별 이용자 수
	 */
	@GetMapping(value = "/subway-weekly", produces = "application/json")
    public List<Long> getWeeklyData() {
        return chartService.getWeeklySubwayCount();
    }
}
