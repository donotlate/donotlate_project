package com.late.donot.admin.model.service;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.late.donot.admin.model.mapper.AdminMapper;
import com.late.donot.board.model.dto.Board;
import com.late.donot.member.model.dto.Member;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminServiceImpl implements AdminService {
	
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Autowired
	private AdminMapper mapper; 
	
	// 로그인
	@Override
	public Member login(Member inputMember) {

		
	Member loginMember = mapper.login(inputMember.getMemberEmail());
		
	if(loginMember == null) return null;
		
		if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) return null;
		
		loginMember.setMemberPw(null);
		return loginMember;
		

	}

	// 유저 조회
	@Override
	public List<Member> getUsers() {
		return mapper.getUsers(); 
	}

	// 유저 수정
	@Override
	public int editUser(Member member) {
		return mapper.editUser(member); 
	}

	// 유저 삭제
	@Override
	public List<Member> removeUser(int memberNo) {
		
		mapper.removeUser(memberNo);
		
		return mapper.getUsers(); 
	}

	// 유저 추가
	@Override
	public List<Member> createUser(Member inputMember) {

		String rawPw =  inputMember.getMemberPw();
		
		String encPw = bcrypt.encode(rawPw);
		
		inputMember.setMemberPw(encPw);		
		
		mapper.createUser(inputMember);
		
		return mapper.getUsers();
	}
	
	
	//--------------------------------------------------------------------------------------------------
	
	@Override
	public List<Board> Notices() {
		return mapper.Notices();
	}

	@Override
	public List<Board> createBoard(Board inputBoard) {
		
		mapper.createBoard(inputBoard);
		return mapper.Notices();
	}

	@Override
	public List<Board> removeNotice(int boardNo) {
		mapper.removeNotice(boardNo);
		return mapper.Notices();
	}

	@Override
	public List<Board> editBoard(Board board) {
		mapper.editBoard(board);
		return mapper.Notices();
	}

	
	@Value("${notice.folder-path}")
	private String noticeFolderPath;   // C:/uploadFiles/board/
	
	@Override
	public String saveNoticeImage(MultipartFile image) throws Exception {

	    String uploadDir = noticeFolderPath; // properties 경로 사용

	    File dir = new File(uploadDir);
	    if (!dir.exists()) dir.mkdirs();

	    String originalName = image.getOriginalFilename();

	    String ext = "";
	    if (originalName != null && originalName.lastIndexOf(".") != -1) {
	        ext = originalName.substring(originalName.lastIndexOf("."));
	    }

	    String savedName = UUID.randomUUID().toString() + ext;

	    File dest = new File(uploadDir + savedName);
	    image.transferTo(dest);

	    return savedName;   // ✅ DB에는 파일명만 저장
	}

}
