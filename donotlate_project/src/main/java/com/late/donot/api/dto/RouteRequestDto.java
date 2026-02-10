package com.late.donot.api.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteRequestDto {
	
	private double sx;
    private double sy;
    private double ex;
    private double ey;

    private String mode;

    private LocalTime departureTime;
    private DayType dayType;
}
