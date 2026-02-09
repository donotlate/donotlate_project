package com.late.donot.board.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.late.donot.board.model.dto.Board;
import com.late.donot.board.model.service.BoardService;
import com.late.donot.member.model.dto.Member;

import feign.Param;
import jakarta.servlet.http.HttpSession;

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
	/** 게시판 생성
	 * @return
	 */
	@PostMapping("createBoard")
	public List<Board> createBoard(@RequestBody Board inputBoard,HttpSession session){
		
	    Member loginMember = (Member) session.getAttribute("loginMember");
	    
	    inputBoard.setMemberNo(loginMember.getMemberNo());

	    return service.createBoard(inputBoard);
	}
	
	/** 게시판 삭제
	 * @param boardNo
	 * @return
	 */
	@DeleteMapping("removeBoard")
	public List<Board> removeNotice(@RequestParam("boardNo")  int boardNo){
		return service.removeNotice(boardNo);
		
	}
	
	/** 게시판 수정
	 * @param board
	 * @return
	 */
	@PutMapping("editBoard")
	public List<Board> editBoard(@RequestBody Board board){
		return service.editBoard(board);
		
	}
	
	
}
