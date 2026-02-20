package com.late.donot.main.model.service;

import com.late.donot.main.model.dto.DashBoardPushDTO;

public interface MainService {

	DashBoardPushDTO getDashboardData(int memberNo);
	
	void updatePushActive(int pushNo, int isActive, int memberNo);

	void deletePush(int pushNo, int memberNo);

}
