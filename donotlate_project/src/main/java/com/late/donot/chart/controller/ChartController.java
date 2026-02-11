package com.late.donot.chart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	@GetMapping(value = "subway-weekly", produces = "application/json")
    public List<Long> getWeeklySubwayData() {
        return chartService.getWeeklySubwayCount();
    }

	/** 작성자 : 유건우
	 * 작성일자 : 2026-02-09
	 * 버스 요일별 이용자 수 (정류장 갯수가 약 4만개라 상위 1000개 정류장 기준으로 채택)
	 */
	@GetMapping(value = "bus-weekly", produces = "application/json")
    public List<Long> getWeeklyBusData() {
        return chartService.getWeeklyBusCount();
    }

	/** 작성자 : 유건우
	 * 작성일자 : 2026-02-10
	 * 환승 많은 노선 Top 10
	 */
	@GetMapping(value = "transfer", produces = "application/json")
	public List<Map<String, Object>> getTransferData(){
		return chartService.getTransferCount();
	}

	/** 작성자 : 유건우
	 * 작성일자 : 2026-02-10
	 * 역 간 거리 긴 구간 Top 10
	 */
	@GetMapping(value = "distance", produces = "application/json")
	public List<Map<String, Object>> getStationDistance() {
		return chartService.getStationDistance();
	}

	/** 작성자 : 유건우
     * 작성일자 : 2026-02-11
     * 지하철 혼잡도 통계 (상위 8개)
     */
	@GetMapping("subway-congestion")
	public List<Map<String, Object>> getSubwayCongestion(@RequestParam("type") String type) {
		return chartService.getSubwayCongestion(type);
	}
}
