package com.late.donot.push.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface PushMapper {

	void mergeSubscription(Map<String, Object> param);

	List<Map<String, Object>> selectByMember(@Param("memberNo")Integer memberNo);
	
	List<Map<String, Object>> selectSchedulerTargets(@Param("nowHHmm") int nowHHmm,
            										 @Param("todayNum") String todayNum);
}
