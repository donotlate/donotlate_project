package com.late.donot.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OdsayApi {
	
	private Result result;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class Result {
		
		private List<OdsayPath> path;
	}
}
