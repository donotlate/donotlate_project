package com.late.donot.chart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "transferClient", url = "https://api.odcloud.kr/api/15062858/v1")
public interface TransferClient {

    @GetMapping("/uddi:8531a97c-4ca9-427f-b30e-bc97eecbc36e")
    String getTransferData(
        @RequestParam("page") int page,
        @RequestParam("perPage") int perPage,
        @RequestParam("serviceKey") String serviceKey
    );
}