package com.late.donot.admin.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.late.donot.board.model.dto.Board;
import com.late.donot.member.model.dto.Member;

public interface AdminService {

	Member login(Member inputMember);

	List<Member> getUsers();

	int editUser(Member member);

	List<Member> removeUser(int memberNo);

	List<Member> createUser(Member inputMember);

	//--------------------------------------------------------------------------------------------------
	
	List<Board> Notices();

	List<Board> createBoard(Board inputBoard);

	List<Board> removeNotice(int boardNo);

	List<Board> editBoard(Board board);

	String saveNoticeImage(MultipartFile image)throws Exception;;
}
