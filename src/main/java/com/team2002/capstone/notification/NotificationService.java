package com.team2002.capstone.notification;

import com.team2002.capstone.domain.Member;
import com.team2002.capstone.dto.AdminNotificationRequestDTO;
import com.team2002.capstone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {
    private final MemberRepository memberRepository;
    private final FcmService fcmService;

    public int sendGlobalNotification(AdminNotificationRequestDTO requestDTO) {
        log.info("관리자 공지 발송 시작: {}", requestDTO.getTitle());

        List<Member> allMembers = memberRepository.findAll();
        List<String> tokens = allMembers.stream()
                .map(Member::getFcmToken)
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            log.warn("발송 대상 토큰이 유효하지 않거나 없어 FCM 전송을 건너뜀.");
            return 0;
        }

        fcmService.sendMulticastNotification(
                tokens,
                requestDTO.getTitle(),
                requestDTO.getBody()
        );

        log.info("총 {}명의 사용자에게 공지 발송.", tokens.size());
        return tokens.size();
    }
}
