package com.late.donot.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.late.donot.admin.model.service.AdminService;
import com.late.donot.board.model.dto.Board;
import com.late.donot.common.config.jwtUtilConfig;
import com.late.donot.member.model.dto.Member;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@RequestMapping("admin")
public class AdminController {

	  @Autowired private AdminService service;
	  @Autowired private jwtUtilConfig jwtUtil;

	  /** 작성자: 양충모 작성일 : 02-19 로그인
	 * @param inputMember
	 * @return
	 */
	@PostMapping("login")
	  public ResponseEntity<?> login(@RequestBody Member inputMember) {

	    Member loginMember = service.login(inputMember);
	    if (loginMember == null) return ResponseEntity.status(401).body("LOGIN_FAIL");

	    String role = "ADMIN";
	    String token = jwtUtil.createToken(loginMember.getMemberEmail(), role);

	    return ResponseEntity.ok(Map.of(
	      "token", token,
	      "memberName", loginMember.getMemberName(),
	      "memberEmail", loginMember.getMemberEmail()
	    ));
	  }

	/**
	 * 작성자: 양충모 작성일: 02-19 로그아웃
	 * 
	 * @param sessionStatus
	 * @return
	 */
	@GetMapping("logout")
	public ResponseEntity<Void> logout() {
	    return ResponseEntity.ok().build();
	}
	
	/** 작성자: 양충모 
	 * 	작성일: 01-29
	 *  유저 조회
	 * @param member	
	 * @return
	 */
	@GetMapping("Users")
	public List<Member> getUsers() {
		
		List<Member> userList = service.getUsers();
		
		return userList;
		
	}
	
	/** 작성자: 양충모
	 *  작성일: 01-30
	 *  유저 수정
	 * @param member
	 * @return
	 */
	@PutMapping("editUser")
	public int editUser(@RequestBody Member member) {
		
		int setuser = service.editUser(member);
		
		return setuser;
		
	}
	
	/** 작성자: 양충모
	 *  작성일: 02-02
	 *  유저 삭제
	 * @param member
	 * @return
	 */
	@DeleteMapping("removeUser")
	public List<Member> removeUser(@RequestParam("memberNo")  int memberNo) {
	    return service.removeUser(memberNo);
	}
	
	/** 작성자: 양충모
	 *  작성일: 02-03
	 *  유저 추가
	 * @param member
	 * @return
	 */
	@PostMapping("createUser")
	public List<Member> createUser(@RequestBody Member inputMember) {
		return service.createUser(inputMember);
	}
	
	//--------------------------------------------------------------------------------------------------
	
	
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
