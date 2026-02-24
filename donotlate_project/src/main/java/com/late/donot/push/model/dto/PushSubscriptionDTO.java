package com.late.donot.push.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushSubscriptionDTO {
	
	private String endpoint;
    private Keys keys;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Keys {
        private String p256dh;
        private String auth;
    }
}
