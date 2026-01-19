package com.late.donot.main.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
	private String headerTitle;       // PC용 헤더 타이틀
    private String headerMobileTitle; // 모바일용 헤더 타이틀
    private String activeMenu;        // 활성화된 메뉴 ID (영어명)
    private boolean isMyPage;         // 마이페이지 여부 (기본값 false)
}
