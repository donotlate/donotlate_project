package com.late.donot.chart.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.chart.model.mapper.ChartMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class ChartServiceImpl implements ChartService {

    @Autowired
    private ChartMapper mapper;

    /** 작성자 : 유건우
	 * 작성일자 : 2026-02-07
	 * 상단 차트 내용 로드
	 */
    @Override
    public Map<String, Object> getTopChart() {
        return mapper.getTopChart();
    }
}
