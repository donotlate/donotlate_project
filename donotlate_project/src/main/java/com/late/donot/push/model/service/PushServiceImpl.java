package com.late.donot.push.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.push.model.dto.PushSubscriptionDTO;
import com.late.donot.push.model.mapper.PushMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class PushServiceImpl implements PushService{
	
	@Autowired
	private PushMapper mapper;

    /** 작성자 : 이승준
     *  작성일 : 2026-02-24
     *  유저의 push를 DB에 저장
     */
    @Override
    public void saveSubscription(PushSubscriptionDTO dto, Integer memberNo, String userAgent) {

        Map<String, Object> param = new HashMap<>();
        param.put("memberNo", memberNo);
        param.put("endpoint", dto.getEndpoint());
        param.put("p256dh", dto.getKeys().getP256dh());
        param.put("auth", dto.getKeys().getAuth());
        param.put("userAgent", userAgent);

        mapper.mergeSubscription(param);
    }
	

}
