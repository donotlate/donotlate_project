package com.late.donot.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.late.donot.admin.model.service.AdminService;
import com.late.donot.board.model.dto.Board;
import com.late.donot.common.config.JwtUtilConfig;
import com.late.donot.member.model.dto.Member;

import jakarta.servlet.http.HttpServletRequest;

@RestController	
@RequestMapping("admin")
public class AdminController {

	  @Autowired private AdminService service;
	  @Autowired private JwtUtilConfig jwtUtil;

	  /** 작성자: 양충모 작성일 : 02-19 로그인
	 * @param inputMember
	 * @return
	 */
	@PostMapping("login")
	  public ResponseEntity<?> login(@RequestBody Member inputMember) {

	    Member loginMember = service.login(inputMember);
	    if (loginMember == null) return ResponseEntity.status(401).body("LOGIN_FAIL");

	    String role = "ADMIN";
	    String accessToken =  jwtUtil.createToken(
	    	    loginMember.getMemberEmail(),
	    	    role,
	    	    loginMember.getMemberNo()
	    	); // 토큰에 번호까지 넣어주기
	    return ResponseEntity.ok(Map.of(
	      "accessToken", accessToken,
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
	@GetMapping("users")
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
	
	/** 게시판 조회 */
	@GetMapping("notices")
	public List<Board> notices() {
	    return service.Notices();
	}

	/** 작성자 : 양충모
	 *  작성일 : 02-24(수정)
	 * 
	 *  게시판 생성 */
	@PostMapping("createBoard")
	public List<Board> createBoard(
	    @RequestParam("boardTitle") String boardTitle,
	    @RequestParam("boardContent") String boardContent,
	    @RequestParam("boardDelFl") String boardDelFl,
	    @RequestParam("categoryNo") int categoryNo,
	    @RequestParam(value = "image", required = false) MultipartFile image,
	    HttpServletRequest request
	) throws Exception {

	    String auth = request.getHeader("Authorization");
	    if (auth == null || !auth.startsWith("Bearer ")) {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "NO_TOKEN");
	    }

	    String token = auth.substring(7);
	    Integer memberNo = jwtUtil.getMemberNoFromToken(token); 
	    if (memberNo == null) {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
	    }

	    Board inputBoard = new Board();
	    inputBoard.setMemberNo(memberNo);
	    inputBoard.setBoardTitle(boardTitle);
	    inputBoard.setBoardContent(boardContent);
	    inputBoard.setBoardDelFl(boardDelFl);
	    inputBoard.setCategoryNo(categoryNo);

	    if (image != null && !image.isEmpty()) {
	        String savedName = service.saveNoticeImage(image);
	        inputBoard.setThumbnailUrl(savedName);
	    }

	    return service.createBoard(inputBoard);
	}

	/** 게시판 삭제 */
	@DeleteMapping("removeBoard")
	public List<Board> removeNotice(@RequestParam("boardNo") int boardNo) {
	    return service.removeNotice(boardNo);
	}

	/** 작성자 : 양충모
	 *  작성일 : 02-24(수정) 
	 * 
	 * 게시판 수정 */
	@PutMapping("editBoard")
	public List<Board> editBoard(
	        @RequestParam("boardNo") int boardNo,
	        @RequestParam("boardTitle") String boardTitle,
	        @RequestParam("boardContent") String boardContent,
	        @RequestParam("boardDelFl") String boardDelFl,
	        @RequestParam("categoryNo") int categoryNo,
	        @RequestParam(value = "image", required = false) MultipartFile image
	) throws Exception {

	    Board board = new Board();
	    board.setBoardNo(boardNo);
	    board.setBoardTitle(boardTitle);
	    board.setBoardContent(boardContent);
	    board.setBoardDelFl(boardDelFl);
	    board.setCategoryNo(categoryNo);

	    // 새 이미지가 오면 저장하고 thumbnailUrl 갱신
	    if (image != null && !image.isEmpty()) {
	        String savedName = service.saveNoticeImage(image);
	        board.setThumbnailUrl(savedName);
	    }
	    // 이미지가 안 오면: SQL에서 thumbnail_url을 업데이트하지 않게 해야 기존 유지됨

	    return service.editBoard(board);
	}
	
}
