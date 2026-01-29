package com.late.donot.mypage.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
