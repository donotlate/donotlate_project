package com.late.donot.push.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.late.donot.push.model.service.PushSendService;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class PushScheduler {
	
	@Autowired
	private PushSendService pushSendService;
	
	@PostConstruct
	public void init() {
	    System.out.println("🔥 Scheduler Bean 생성됨");
	}

	/** 작성자 : 이승준
	 *  작성일 : 2026-02-24
	 *  예약 push 실행
	 */
	@Scheduled(cron = "0 * * * * *")
	public void runScheduler() {

	    int nowHHmm = getNowHHmm();
	    String todayNum = getTodayNum();

	    log.info("🕐 스케줄러 실행: {} / {}", nowHHmm, todayNum);

	    try {
	        pushSendService.sendScheduledPush(nowHHmm, todayNum);
	    } catch (Exception e) {
	        log.error("스케줄 푸시 실패", e);
	    }
	}

    /** 작성자 : 이승준
     *  작성일 : 2026-02-24
     *  현재시간 변환
     */
    private int getNowHHmm() {
        return Integer.parseInt(
                LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))
        );
    }

    /** 작성자 : 이승준
     *  작성일 : 2026-02-24
     *  요일로 변환
     */ 
    private String getTodayNum() {
        return switch (LocalDate.now().getDayOfWeek()) {
            case MONDAY -> "1";
            case TUESDAY -> "2";
            case WEDNESDAY -> "3";
            case THURSDAY -> "4";
            case FRIDAY -> "5";
            case SATURDAY -> "6";
            case SUNDAY -> "7";
        };
    }
}