package com.team2002.capstone.notification;

import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.ReadingHabit;
import com.team2002.capstone.repository.HabitTrackerRepository;
import com.team2002.capstone.repository.MemberRepository;
import com.team2002.capstone.repository.ReadingHabitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final ReadingHabitRepository readingHabitRepository;
    private final HabitTrackerRepository habitTrackerRepository;
    private final MemberRepository memberRepository;
    private final FcmService fcmService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void checkPersonalReadingTime() {
        ZonedDateTime now = ZonedDateTime.now(KST);
        String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        LocalDate today = LocalDate.now(KST);

        String currentDay = now.getDayOfWeek().name().substring(0, 3); // 현재 요일을 문자열로 변환 (MON, TUE ...)

        log.info("checkPersonalReadingTime: 시간 {}, 요일 {}", currentTime, currentDay);

        List<ReadingHabit> habits = readingHabitRepository.findActiveHabitsByTimeAndDay(currentTime, currentDay);

        for (ReadingHabit habit : habits) {
            if (habit.getLastNotifiedDate() != null && today.isEqual(habit.getLastNotifiedDate())) {
                log.info("사용자 ID {} - 오늘 이미 알림이 발송되었습니다.", habit.getMember().getId());
                continue;
            }
            Member member = memberRepository.findById(habit.getMember().getId()).orElse(null);
            if (member != null && member.getFcmToken() != null && !member.getFcmToken().isEmpty()) {
                fcmService.sendNotification(
                        member.getFcmToken(),
                        "독서 시간입니다!",
                        "꾸준한 독서 습관을 만들어 보세요!"
                );
            }
            habit.updateLastNotifiedDate(today);
            log.info("사용자 ID {}에게 개인 알림 발송 완료", habit.getMember().getId());
        }
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 22 * * *")
    public void sendHabitTrackerReminder() {
        log.info("check habitTrackerReminder (22:00)");
        LocalDate today = LocalDate.now(KST);

        List<Long> memberIdsToNotify = habitTrackerRepository.findMemberIdsWithoutRecordForToday(today);
        if (memberIdsToNotify.isEmpty()) {
            log.info("모든 사용자가 오늘 독서 기록을 완료했습니다. 알림을 보내지 않습니다.");
            return;
        }
        List<Member> membersToNotify = memberRepository.findAllById(memberIdsToNotify);

        List<String> tokens = membersToNotify.stream()
                .map(Member::getFcmToken)
                .filter(token -> token != null && !token.isEmpty())
                .toList();
        if (tokens.isEmpty()) {
            log.warn("미기록 사용자 {}명 중 유효한 FCM 토큰이 없습니다.", memberIdsToNotify.size());
            return;
        }

        fcmService.sendMulticastNotification
                (tokens,
                        "📚 오늘 독서 체크하셨나요?",
                        "잊지 말고 독서 현황을 기록해주세요!"
                );
        log.info("총 {}명의 미기록 사용자에게 해빗 트래커 알림을 발송했습니다.", tokens.size());
    }
}
