package com.late.donot.calculator.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.late.donot.calculator.model.dto.PushSaveRequest;

@Mapper
public interface CalculatorMapper {

	void insertRoute(PushSaveRequest request);

	int insertPush(PushSaveRequest request);

}
