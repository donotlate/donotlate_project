package com.late.donot.board.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.late.donot.board.model.dto.Board;
import com.late.donot.board.model.service.BoardService;

@Controller
public class BoardController {

    @Autowired
    private BoardService service;

    @GetMapping("/notice")
    public String noticeList(Model model, 
                             @RequestParam(value="cp", required=false, defaultValue="1") int cp,
                             @RequestParam(value="query", required=false) String query) {

        // 1. 검색어 유무에 따른 전체 게시글 수 조회
        // (검색어가 있으면 검색된 결과의 개수를 가져와야 totalPages가 정확해집니다)
        int totalCount = service.getListCount(query); 
        
        int limit = 5; 
        int totalPages = (int) Math.ceil((double) totalCount / limit);

        // 2. 검색어와 함께 리스트 조회
        List<Board> noticeList = service.selectNoticeList(cp, limit, query); 

        // 3. 모델에 데이터 담기
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("currentPage", cp);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("query", query); 
        model.addAttribute("activeMenu", "notice");
        
        return "notice"; 
    }
}