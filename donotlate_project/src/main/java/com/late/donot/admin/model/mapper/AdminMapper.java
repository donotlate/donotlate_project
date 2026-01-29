package com.late.donot.admin.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.late.donot.member.model.dto.Member;

@Mapper
public interface AdminMapper {

	Member login(String memberEmail);

}
