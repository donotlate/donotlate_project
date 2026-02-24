package com.late.donot.push.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.late.donot.member.model.dto.Member;
import com.late.donot.push.model.dto.PushSubscriptionDTO;
import com.late.donot.push.model.service.PushSendService;
import com.late.donot.push.model.service.PushService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/push")
@Slf4j
public class PushController {

	@Autowired
	private PushService pushService;

	@Autowired
	private PushSendService pushSendService;
	
    /** 작성자 : 이승준
     *  작성일 : 2026-02-24
     *  db에 푸쉬저장
     */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody PushSubscriptionDTO dto,
                                       @SessionAttribute(value="loginMember", required=false) Member member,
                                       HttpServletRequest request) {

    	Integer memberNo = member != null ? member.getMemberNo() : null;

        pushService.saveSubscription(
                dto,
                memberNo,
                request.getHeader("User-Agent")
        );

        return ResponseEntity.ok().build();
    }
    
    /** 작성자 : 이승준
     *  작성일 : 2026-02-24
     *  DB보고 푸쉬발송
     */
    @GetMapping("/test")
    public String testPush(
            @SessionAttribute("loginMember") Member member
    ) throws Exception {

        pushSendService.sendTestPush(member.getMemberNo());

        return "테스트 푸시 전송 완료";
    }
    
}
