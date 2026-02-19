package com.late.donot.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.late.donot.main.model.dto.Menu;
import com.late.donot.main.model.type.MenuType;

@Controller
@RequestMapping("/")
public class MainController {
    
    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-19
     * 회원가입 페이지 이동
     */
    @GetMapping("signUp")
    public String signUp() {
        return "signUp";
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-19
     * 메인페이지 이동
     */
    @GetMapping("main")
    public String main(Model model) {
        setMenu(model, MenuType.MAIN);
        return "main";
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-19
     * 계산 페이지 이동
     */
    @GetMapping("calculator")
    public String calculator(Model model) {
        setMenu(model, MenuType.CALCULATOR);
        return "calculator";
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-19
     * 통계 페이지 이동
     */
    @GetMapping("chart")
    public String chart(Model model) {
        setMenu(model, MenuType.CHART);
        return "chart";
    }

    @GetMapping("weather")
    public String weather(Model model) {
        setMenu(model, MenuType.WEATHER);
        return "weather";
    }


    @GetMapping("mypage")
    public String mypage(Model model) {
        setMenu(model, MenuType.MYPAGE);
        return "mypage";
    }
    

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-19
     * Enum 정보를 바탕으로 Model에 속성을 대입
     */
    private void setMenu(Model model, MenuType menuType) {
        Menu menu = menuType.toMenuDto();

        model.addAttribute("headerTitle", menu.getHeaderTitle());
        model.addAttribute("headerMobileTitle", menu.getHeaderMobileTitle());
        model.addAttribute("activeMenu", menu.getActiveMenu());
        
        if (menu.isMyPage()) {
            model.addAttribute("isMyPage", true);
        }
    }
}
