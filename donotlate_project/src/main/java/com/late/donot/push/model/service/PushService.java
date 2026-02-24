package com.late.donot.push.model.service;

import com.late.donot.push.model.dto.PushSubscriptionDTO;

public interface PushService {

	void saveSubscription(PushSubscriptionDTO dto, Integer memberNo, String header);

}
