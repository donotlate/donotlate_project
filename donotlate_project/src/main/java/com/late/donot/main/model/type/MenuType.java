package com.late.donot.main.model.type;

import com.late.donot.main.model.dto.Menu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuType {
    MAIN("메인페이지", "메인페이지", "main"),
    CALCULATOR("시간계산 & Push 등록", "시간계산 & Push 등록", "calculator"),
    CHART("통계 대시보드", "통계 대시보드", "chart"),
    WEATHER("현재 날씨", "현재 날씨", "weather"),
    NOTICE("공지 사항", "공지 사항", "notice"),
    MYPAGE("마이페이지", "마이페이지", "mypage");

    private final String title;       // 메인 타이틀
    private final String mobileTitle; // 모바일 타이틀
    private final String code;        // 활성화된 메뉴 ID (영어명)

    // 2. Enum 데이터를 DTO로 변환하는 메서드
    public Menu toMenuDto() {
        return Menu.builder()
                .headerTitle("늦지마 / " + this.title)  // 공통 단어 자동 추가
                .headerMobileTitle(this.mobileTitle)
                .activeMenu(this.code)
                .isMyPage(this == MYPAGE)               // MYPAGE일 때만 true 설정
                .build();
    }
}