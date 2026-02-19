package com.late.donot.calculator.model.service;

import com.late.donot.api.dto.Route;

public interface AiService {

    String recommendPushTime(Route route, int prepareTime,  int bufferTime, String weather, String departureDateTime);
}
