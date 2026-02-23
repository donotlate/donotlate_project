package com.late.donot.main.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DashBoardPushDTO {
	
	private List<PushSimpleDTO> pushList;
    private Integer averagePushTime;
    private Double rankPercent;
    
    private String aiComment;
}
