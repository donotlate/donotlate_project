package com.late.donot.admin.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.late.donot.member.model.dto.Member;

@Mapper
public interface AdminMapper {

	Member login(String memberEmail);

	List<Member> getUsers();

	int editUser(Member member);

	void removeUser(int memberNo);

	void createUser(Member inputMember);


}
