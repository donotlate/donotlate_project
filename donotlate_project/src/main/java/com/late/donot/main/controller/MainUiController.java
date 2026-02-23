package com.late.donot.main.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.late.donot.main.model.dto.DashBoardPushDTO;
import com.late.donot.main.model.service.MainAiService;
import com.late.donot.main.model.service.MainService;
import com.late.donot.member.model.dto.Member;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("ui")
@Slf4j
public class MainUiController {
	
	@Autowired
	private MainService service;
	
	@Autowired
	private MainAiService mainAiService;
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-20
	 *  사용자 board 데이터 조회후 반환
	 */
	@GetMapping("dashboard")
	public DashBoardPushDTO getDashboard(HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return null;

        return service.getDashboardData(loginMember.getMemberNo());
    }
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-20
	 *  알람의 활성화/비활성화
	 */
	@PatchMapping("/push/{pushNo}/active")
	public void updateActive(@PathVariable("pushNo") int pushNo,
	                         @RequestBody Map<String, Integer> body,
	                         HttpSession session) {

	    Member loginMember = (Member) session.getAttribute("loginMember");

	    service.updatePushActive(pushNo, body.get("isActive"), loginMember.getMemberNo());
	}
	
	/** 작성자 : 이승준
	 *  작성일 : 2026-02-20
	 *  알람 삭제
	 */
	@DeleteMapping("/push/{pushNo}")
	public void deletePush(@PathVariable("pushNo") int pushNo,
	                       HttpSession session) {

	    Member loginMember = (Member) session.getAttribute("loginMember");

	    service.deletePush(pushNo, loginMember.getMemberNo());
	}
	
	@PostMapping("/dashboard/ai")
	public String recommendDashboardAi(@RequestParam("lat") double lat,
									   @RequestParam("lon") double lon) {

	    return mainAiService.generateDashboardComment(lat, lon);
	}
	
		
}
