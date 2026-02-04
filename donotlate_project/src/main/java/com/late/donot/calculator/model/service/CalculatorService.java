package com.late.donot.calculator.model.service;

import java.util.List;

import com.late.donot.api.dto.Route;

public interface CalculatorService {
	
	List<Route> findRoute(double sx, double sy, double ex, double ey, String mode);

}
