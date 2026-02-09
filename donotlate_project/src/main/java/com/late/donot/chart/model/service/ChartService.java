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
	 * 작성일자 : 2026-02-07
	 * 지하철 요일별 이용자 수
	 */
    List<Long> getWeeklySubwayCount();

}
