package com.late.donot.main.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.late.donot.main.model.dto.PushSimpleDTO;


@Mapper
public interface MainMapper {

	List<PushSimpleDTO> selectPushList(int memberNo);

	Integer selectAveragePushTime(int memberNo);
	
	void updatePushActive(@Param("pushNo") int pushNo,
            			  @Param("isActive") int isActive,
            			  @Param("memberNo") int memberNo);
	
	void deletePush(@Param("pushNo") int pushNo,
                    @Param("memberNo") int memberNo);

}
