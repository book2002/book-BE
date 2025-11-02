package com.team2002.capstone.notification;

import com.team2002.capstone.domain.BookLoan;
import com.team2002.capstone.domain.Member;
import com.team2002.capstone.repository.BookLoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookLoanScheduler {

    private final BookLoanRepository bookLoanRepository;
    private final FcmService fcmService; // (FcmService가 @Service로 등록되어 있어야 함)
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");


    @Transactional(readOnly = true)
    @Scheduled(cron = "* * 10 * * *") // 매일 오전 10시 0분 0초
    public void sendBookLoanDueDateReminders() {
        LocalDate today = LocalDate.now(KST);
        log.info("Checking for book loan due dates for: {}", today);

        // 아직 반납 안 된 대출 기록 조회
        List<BookLoan> loansDueToday = bookLoanRepository.findLoansDueToday(today);

        if (loansDueToday.isEmpty()) {
            log.info("No book loans due today.");
            return;
        }

        log.info("Found {} loans due today. Sending notifications...", loansDueToday.size());

        // 각 사용자에게 알림 발송
        for (BookLoan loan : loansDueToday) {
            try {
                // BookLoan -> Profile -> Member -> FcmToken
                Member member = loan.getProfile().getMember();

                if (member != null && member.getFcmToken() != null && !member.getFcmToken().isEmpty()) {

                    // 알림 메시지 생성 및 발송
                    String title = "도서 반납 D-DAY";
                    String body = "오늘이 '" + loan.getBookTitle() + "' 책의 반납 예정일입니다! 잊지 말고 반납해주세요.";

                    fcmService.sendNotification(
                            member.getFcmToken(),
                            title,
                            body
                    );
                    log.info("사용자 ID {}에게 도서 반납 알림 발송 완료 (Loan ID: {})", member.getId(), loan.getLoanId());

                } else {
                    log.warn("사용자 ID {}의 FCM 토큰이 없어 알림을 발송할 수 없습니다. (Loan ID: {})", (member != null ? member.getId() : "Unknown"), loan.getLoanId());
                }
            } catch (Exception e) {
                // 특정 사용자 알림 실패가 다른 사용에게 영향을 주지 않도록 try-catch
                log.error("Error sending due date notification for Loan ID {}: {}", loan.getLoanId(), e.getMessage());
            }
        }
    }
}
