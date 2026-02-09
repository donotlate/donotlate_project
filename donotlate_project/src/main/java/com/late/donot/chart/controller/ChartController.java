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
	 * 작성일자 : 2026-02-09
	 * 지하철 요일별 이용자 수
	 */
	@GetMapping(value = "/subway-weekly", produces = "application/json")
    public List<Long> getWeeklySubwayData() {
        return chartService.getWeeklySubwayCount();
    }

	/** 작성자 : 유건우
	 * 작성일자 : 2026-02-09
	 * 버스 요일별 이용자 수 (정류장 갯수가 약 4만개라 상위 1000개 정류장 기준으로 채택)
	 */
	@GetMapping(value = "/bus-weekly", produces = "application/json")
    public List<Long> getWeeklyBusData() {
        return chartService.getWeeklyBusCount();
    }
}
