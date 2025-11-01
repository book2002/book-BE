package com.team2002.capstone.controller;

// ▼▼▼ "PrincipalDetails" 관련 import가 다시 필요합니다 ▼▼▼
import com.team2002.capstone.config.auth.CustomOauth2UserDetails;
import com.team2002.capstone.dto.BookLoanResponseDto;
import com.team2002.capstone.dto.BookLoanSaveRequestDto;
import com.team2002.capstone.service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // import 활성화
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/loans") // 대출 기록 API 경로
public class BookLoanController {

    private final BookLoanService bookLoanService;


     // 새로운 대출 기록 저장
    @PostMapping("/items")
    public ResponseEntity<BookLoanResponseDto> saveLoan(
            @AuthenticationPrincipal CustomOauth2UserDetails userDetails,
            @Valid @RequestBody BookLoanSaveRequestDto requestDto) {

        //실제 로그인한 사용자의 ID
        Long profileId = userDetails.getMember().getProfile().getId();

        BookLoanResponseDto responseDto = bookLoanService.saveLoan(profileId, requestDto);
        return ResponseEntity.ok(responseDto);
    }


     // 내 대출 기록 목록 조회
    @GetMapping
    public ResponseEntity<List<BookLoanResponseDto>> getMyLoans(
            // ▼▼▼ @AuthenticationPrincipal을 다시 사용합니다 ▼▼▼
            @AuthenticationPrincipal CustomOauth2UserDetails userDetails) {

        Long profileId = userDetails.getMember().getProfile().getId();
        List<BookLoanResponseDto> myLoans = bookLoanService.getMyLoans(profileId);
        return ResponseEntity.ok(myLoans);
    }

    // 대출 기록 '반납 완료'로 상태 변경
    @PatchMapping("/{loanId}/return")
    public ResponseEntity<BookLoanResponseDto> markAsReturned(
            // ▼▼▼ @AuthenticationPrincipal을 다시 사용합니다 ▼▼▼
            @AuthenticationPrincipal CustomOauth2UserDetails userDetails,
            @PathVariable Long loanId) {

        Long profileId = userDetails.getMember().getProfile().getId();
        BookLoanResponseDto updatedLoan = bookLoanService.markAsReturned(profileId, loanId);
        return ResponseEntity.ok(updatedLoan);
    }

    // 대출기록 삭제
    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> deleteLoan(
            // ▼▼▼ @AuthenticationPrincipal을 다시 사용합니다 ▼▼▼
            @AuthenticationPrincipal CustomOauth2UserDetails userDetails,
            @PathVariable Long loanId) {

        Long profileId = userDetails.getMember().getProfile().getId();
        bookLoanService.deleteLoan(profileId, loanId);
        return ResponseEntity.noContent().build();
    }
}

