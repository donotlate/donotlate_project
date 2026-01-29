package com.late.donot.mypage.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RequestMapping("mypage")
public class MyPageController {

    @GetMapping("nameChange")
    @ResponseBody
    private String nameChange(){
        return "";
    }
}
