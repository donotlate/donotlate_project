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

import jakarta.servlet.http.HttpSession;

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
        int totalPages = (int) Math.ceil((double) totalCount / limit);// Math.ceil : 반올림 해줌


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
    public String noticeDetail(@PathVariable("no") int boardNo, Model model,HttpSession session) {
    	
    	Board notice = service.selectNoticeDetail(boardNo);
    	
    	// ex) 이게 없으면 첫번째 본 게시글은 올라가지만 다른게시글을 처음 봤을때 올라가지 않음 이미 키값은 true
    	String key = "viewed_" + boardNo; 
    	
    	//  조회수 증가(세션)
    	if(session.getAttribute(key) == null) {
    		service.increaseViewCount(boardNo);
    		session.setAttribute(key,"true");
    	}
    	
    	// 헤더 정보를 위한 값 추가
        model.addAttribute("activeMenu", "notice");       
        model.addAttribute("headerTitle", "늦지마 / 공지 사항");      
        model.addAttribute("headerMobileTitle", "공지");   
    	
    	model.addAttribute("notice", notice);
        return "noticeDetail";
    }
    
    
    
    
}