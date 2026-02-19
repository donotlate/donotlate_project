package com.late.donot.calculator.model.dto;

import com.late.donot.api.dto.Route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiRequestDTO {
	
	private Route route;
    private int prepareTime;
    private int bufferTime;
    private String weather;
    private String departureDateTime;

}
