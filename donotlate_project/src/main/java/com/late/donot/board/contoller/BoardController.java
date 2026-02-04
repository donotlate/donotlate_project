package com.late.donot.board.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.late.donot.board.model.dto.Board;
import com.late.donot.board.model.service.BoardService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("admin")
public class BoardController {

	@Autowired
	private BoardService service;
	
	
	/** 게시판 조회
	 * @return
	 */
	@GetMapping("Notices")
	public List<Board> Notices(){
		
		List<Board> boardList = service.Notices();
		
		return boardList;

	}
}
