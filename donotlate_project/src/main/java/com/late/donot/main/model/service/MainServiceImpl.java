package com.late.donot.main.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.main.model.dto.DashBoardPushDTO;
import com.late.donot.main.model.dto.PushSimpleDTO;
import com.late.donot.main.model.mapper.MainMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MainServiceImpl implements MainService{
	
	@Autowired
	private MainMapper mapper;

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 *  푸시목록과 시간으로 DTO조합
	 */
	@Override
	public DashBoardPushDTO getDashboardData(int memberNo) {
		
		List<PushSimpleDTO> pushList = mapper.selectPushList(memberNo);
        Integer avg = mapper.selectAveragePushTime(memberNo);

        return DashBoardPushDTO.builder()
        		.pushList(pushList)
                .averagePushTime(avg)
                .build();
		
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 *  알람상태 수정
	 */
	@Override
	public void updatePushActive(int pushNo, int isActive, int memberNo) {
	    mapper.updatePushActive(pushNo, isActive, memberNo);
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-19(수정)
	 *  알람삭제
	 */
	@Override
	public void deletePush(int pushNo, int memberNo) {
		mapper.deletePush(pushNo, memberNo);
		
	}

}
