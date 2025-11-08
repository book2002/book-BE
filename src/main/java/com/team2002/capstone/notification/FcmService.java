package com.team2002.capstone.notification;

import com.google.firebase.messaging.*;
import com.team2002.capstone.domain.Member;
import com.team2002.capstone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
    private MemberRepository memberRepository;

    /*
    * 독서 습관 설정 기능, 신고 처리 기능에 사용
    */
    public void sendNotification(String target, String title, String body) {

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(target)
                .setNotification(notification)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            log.info("FCM 단일 알림 전송 성공 (토큰: {})", target);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 단일 알림 전송 실패 (토큰: {}): {}", target, e.getMessage());
            // 실패한 토큰 처리
        }
    }

    /*
    * 해빗 트래커 기능, 전체 알림 발송에 사용
    */
    public void sendMulticastNotification(List<String> targetTokens, String title, String body) {
        if (targetTokens.isEmpty()) return;

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(targetTokens)
                .setNotification(notification)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

            if (response.getFailureCount() > 0) {
                log.warn("FCM 멀티캐스트 실패: {}개 성공, {}개 실패.",
                        response.getSuccessCount(), response.getFailureCount());

                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    SendResponse sendResponse = responses.get(i);
                    if (!sendResponse.isSuccessful()) {
                        String failedToken = targetTokens.get(i);
                        String errorCode = sendResponse.getException().getErrorCode().name();

                        if (errorCode.equals("UNREGISTERED") || errorCode.equals("INVALID_ARGUMENT")) {
                            Optional<Member> optionalMember = memberRepository.findByFcmToken(failedToken);
                            optionalMember.ifPresent(m -> {
                                m.setFcmToken(null);
                                memberRepository.save(m);
                                log.warn("유효하지 않은 FCM 토큰 제거 완료. Member ID: {}", m.getId());
                            });
                        }
                    }
                }
            } else {
                log.info("FCM 멀티캐스트 전송 성공: 총 {}명에게 발송 완료.", response.getSuccessCount());
            }
        } catch (Exception e) {
            log.error("FCM 멀티캐스트 전송 중 오류 발생: {}", e.getMessage());
        }
    }
}
