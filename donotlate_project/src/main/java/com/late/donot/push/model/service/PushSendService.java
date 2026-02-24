package com.late.donot.push.model.service;

public interface PushSendService {

	void sendTestPush(Integer memberNo) throws Exception;
	
	void sendScheduledPush(int nowHHmm, String todayKor) throws Exception;

}
