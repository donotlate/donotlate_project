package com.late.donot.push.model.service;

import java.security.Security;
import java.util.List;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.push.model.mapper.PushMapper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class PushSendServiceImpl implements PushSendService {

    @Value("${push.vapid.public-key}")
    private String publicKey;

    @Value("${push.vapid.private-key}")
    private String privateKey;

    @Value("${push.vapid.subject}")
    private String subject;

    @Autowired
    private PushMapper pushMapper;
    
    @PostConstruct 
    public void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /** 작성자 : 이승준
     *  작성일 : 2026-02-24
     *  테스트용 push 확인
     */
    @Override
    public void sendTestPush(Integer memberNo) throws Exception {

        List<Map<String,Object>> subs =
                pushMapper.selectByMember(memberNo);

        PushService pushService = new PushService()
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .setSubject(subject);

        String payload = """
        {
          "title": "🔥 로컬 테스트 성공",
          "body": "푸시 전송이 정상 작동합니다."
        }
        """;

        for (Map<String,Object> sub : subs) {

            Notification notification = new Notification(
                    (String) sub.get("ENDPOINT"),
                    (String) sub.get("P256DH"),
                    (String) sub.get("AUTH"),
                    payload
            );

            pushService.send(notification);
        }
    }
    
    /** 작성자 : 이승준
     *  작성일 : 2026-02-24
     *  실제 push 발송 
     */
    @Override
    public void sendScheduledPush(int nowHHmm, String todayKor) throws Exception {

        List<Map<String,Object>> targets =
                pushMapper.selectSchedulerTargets(nowHHmm, todayKor);

        if (targets.isEmpty()) return;

        PushService pushService = new PushService()
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .setSubject(subject);

        for (Map<String,Object> push : targets) {

        	Integer memberNo = ((Number) push.get("MEMBER_NO")).intValue();

            List<Map<String,Object>> subs =
                    pushMapper.selectByMember(memberNo);

            String payload = """
            {
              "title": "⏰ 출발 준비하세요!",
              "body": "%s 알림 시간입니다."
            }
            """.formatted(push.get("PUSH_NAME"));

            for (Map<String,Object> sub : subs) {

                Notification notification = new Notification(
                        (String) sub.get("ENDPOINT"),
                        (String) sub.get("P256DH"),
                        (String) sub.get("AUTH"),
                        payload
                );

                pushService.send(notification);
            }
        }
    }
    
}