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
                             @RequestParam(value="cp", required=false, defaultValue="1") int cp) {


        int totalCount = service.getListCount(); 
        int limit = 5; 
        

        int totalPages = (int) Math.ceil((double) totalCount / limit);

        List<Board> noticeList = service.selectNoticeList(cp, limit); 

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("currentPage", cp);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activeMenu", "notice");
        
        return "notice"; 
    }
}