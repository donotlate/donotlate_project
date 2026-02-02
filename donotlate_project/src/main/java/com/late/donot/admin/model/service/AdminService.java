package com.late.donot.admin.model.service;

import java.util.List;

import com.late.donot.member.model.dto.Member;

public interface AdminService {

	Member login(Member inputMember);

	List<Member> getUsers();

	int editUser(Member member);

	List<Member> removeUser(int memberNo);


}
