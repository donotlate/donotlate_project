package com.late.donot.mypage.model.service;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.late.donot.member.model.dto.Member;
import com.late.donot.mypage.model.mapper.MyPageMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MypageServiceImpl implements MyPageService {

    @Autowired
    private MyPageMapper myPageMapper;
    
    @Autowired
	private BCryptPasswordEncoder bcrypt;

    @Value("${profile.folder-path}")
    private String folderPath;

    @Value("${profile.web-path}")
    private String webPath;
    
    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-29
     * 마이페이지 - 이름변경
     */
    @Override
    public boolean nameChange(String changedName, int memberNo) {
        return myPageMapper.nameChange(changedName, memberNo);
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-29
     * 마이페이지 - 비밀번호 변경
     */
    @Override
    public boolean changePw(Map<String, Object> data, int memberNo) {
        // 현재 비밀번호가 일치하는지 확인하기
		String originPw = myPageMapper.selectPw(memberNo);

		// 다를 경우
		if (!bcrypt.matches((String) data.get("currentPw"), originPw)) {
            return false;
        }

        // 같을 경우
		String encPw = bcrypt.encode((String) data.get("newPw"));

		data.put("encPw", encPw);
		data.put("memberNo", memberNo);

		return myPageMapper.changePw(data);
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-30
     * 마이페이지 - 회원탈퇴
     */
    @Override
    public boolean deleteMember(int memberNo, String deletePW) {
        String originPw = myPageMapper.selectPw(memberNo);

		// 다를 경우
		if (!bcrypt.matches(deletePW, originPw)) {
            return false;
        }

        return myPageMapper.deleteMember(memberNo);
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-02-02
     * 마이페이지 - 프로필 사진 변경
     */
    @Override
    public int saveProfileImage(Member loginMember, String status, MultipartFile profileImg) throws Exception {
        int result = 0;
        String oldPath = loginMember.getProfileImg();
        String rename = null;

        // 1. 상태별 분기 처리
        if (status.equals("keep")) {
            return 1; // 아무것도 안 함
        }

        if (status.equals("delete")) {
            loginMember.setProfileImg(null);
        } 
        
        else if (status.equals("change")) {
            // 새 파일명 생성 (내부 메서드 활용)
            rename = createRename(profileImg.getOriginalFilename());
            // DB에 저장할 웹 경로 설정
            loginMember.setProfileImg(webPath + rename);
        }

        // 2. DB 업데이트 실행
        result = myPageMapper.updateProfileImg(loginMember);

        // 3. DB 업데이트 성공 시 실제 서버 파일 처리
        if (result > 0) {
            // [변경 상황] 새 파일 저장 및 이전 파일 삭제
            if (status.equals("change")) {
                profileImg.transferTo(new File(folderPath + rename));
                if (oldPath != null) deletePhysicalFile(oldPath);
            }
            // [삭제 상황] 이전 파일만 삭제
            else if (status.equals("delete") && oldPath != null) {
                deletePhysicalFile(oldPath);
            }
        }

        return result;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-02-02
     * 파일명 변경 로직
     */
    private String createRename(String originalFileName) {
        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
        // UUID를 사용하면 중복 위험이 사실상 0%이며 로직이 간결합니다.
        return UUID.randomUUID().toString() + ext;
    }

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-02-02
     * 서버 내 물리 파일 삭제
     */
    private void deletePhysicalFile(String webPathStr) {
        String fileName = webPathStr.replace(webPath, "");
        File file = new File(folderPath + fileName);
        if (file.exists()) file.delete();
    }

}
