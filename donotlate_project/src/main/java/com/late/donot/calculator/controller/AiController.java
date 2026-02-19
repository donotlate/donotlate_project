package com.late.donot.calculator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.late.donot.calculator.model.dto.AiRequestDTO;
import com.late.donot.calculator.model.service.AiService;

@RestController
@RequestMapping("/push")
public class AiController {

    @Autowired
    private AiService aiService;

    /** 작성자 : 이승준
     *  작성일 : 2026-02-19(수정)
     *  AI가 최적 Push 알림 시각을 계산해 반환
     */
    @PostMapping("/ai")
    public String recommendPush(@RequestBody AiRequestDTO request) {

        return aiService.recommendPushTime(
                request.getRoute(),
                request.getPrepareTime(),
                request.getBufferTime(),
                request.getWeather(),
                request.getDepartureDateTime()
        );
    }

}
