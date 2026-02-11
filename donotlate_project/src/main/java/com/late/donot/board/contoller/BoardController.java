package com.late.donot.board.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.late.donot.board.model.dto.Board;
import com.late.donot.board.model.service.BoardService;

@Controller
public class BoardController {

    @Autowired
    private BoardService service;

    /** 작성자: 양충모
     *  작성일: 2026-02-11
     *  공지 사항
     */
    @GetMapping("/notice")
    public String noticeList(Model model, 
                             @RequestParam(value="cp", required=false, defaultValue="1") int cp,
                             @RequestParam(value="query", required=false) String query) {


        int totalCount = service.getListCount(query); 
        
        int limit = 5; 
        int totalPages = (int) Math.ceil((double) totalCount / limit);


        List<Board> noticeList = service.selectNoticeList(cp, limit, query); 


        model.addAttribute("noticeList", noticeList);
        model.addAttribute("currentPage", cp);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("query", query); 
        model.addAttribute("activeMenu", "notice");
        
        
       // 헤더 정보를 위한 값 추가      
        model.addAttribute("headerTitle", "늦지마 / 공지 사항");      
        model.addAttribute("headerMobileTitle", "공지");   
        
        return "notice"; 
    }
    
    /** 작성자: 양충모
     *  작성일: 2026-02-11
     *  자세한 공지 사항
     */
    @GetMapping("/notice/{no}")
    public String noticeDetail(@PathVariable("no") int boardNo, Model model) {
    	
    	Board notice = service.selectNoticeDetail(boardNo);
    	
    	// 헤더 정보를 위한 값 추가
        model.addAttribute("activeMenu", "notice");       
        model.addAttribute("headerTitle", "늦지마 / 공지 사항");      
        model.addAttribute("headerMobileTitle", "공지");   
    	
    	model.addAttribute("notice", notice);
        return "noticeDetail";
    }
    
    
    
    
}