package com.late.donot.calculator.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="odsayBusTime")
public interface OdsayBusTime {

}
