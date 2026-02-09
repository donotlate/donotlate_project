package com.late.donot.chart.model.service;

import java.util.List;
import java.util.Map;

public interface ChartService {

    /** 작성자 : 유건우
	 * 작성일자 : 2026-02-07
	 * 상단 차트 내용 로드
	 */
    Map<String, Object> getTopChart();

	/** 작성자 : 유건우
	 * 작성일자 : 2026-02-09
	 * 지하철 요일별 이용자 수
	 */
    List<Long> getWeeklySubwayCount();

	/** 작성자 : 유건우
	 * 작성일자 : 2026-02-09
	 * 버스 요일별 이용자 수 (정류장 갯수가 약 4만개라 상위 1000개 정류장 기준으로 채택)
	 */
    List<Long> getWeeklyBusCount();

}
