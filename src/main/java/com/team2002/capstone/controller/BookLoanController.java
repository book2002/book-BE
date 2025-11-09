package com.team2002.capstone.controller;

// ▼▼▼ Security 관련 import 모두 삭제 ▼▼▼
// import com.team2002.capstone.config.auth.CustomOauth2UserDetails;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.team2002.capstone.dto.BookLoanResponseDto;
import com.team2002.capstone.dto.BookLoanSaveRequestDto;
import com.team2002.capstone.dto.BookLoanUpdateRequestDto;
import com.team2002.capstone.service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/loans")
public class BookLoanController {

    private final BookLoanService bookLoanService;

    // 대출 기록 저장
    @PostMapping
    public ResponseEntity<BookLoanResponseDto> saveLoan(
            @Valid @RequestBody BookLoanSaveRequestDto requestDto) { // @AuthenticationPrincipal 삭제
        BookLoanResponseDto responseDto = bookLoanService.saveLoan(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 대출 기록 조회
    @GetMapping
    public ResponseEntity<List<BookLoanResponseDto>> getMyLoans() { // @AuthenticationPrincipal 삭제

        List<BookLoanResponseDto> myLoans = bookLoanService.getMyLoans();
        return ResponseEntity.ok(myLoans);
    }

    // 반납 완료 상태 변경
    @PatchMapping("/{loanId}/return")
    public ResponseEntity<BookLoanResponseDto> markAsReturned(
            @PathVariable Long loanId) { // @AuthenticationPrincipal 삭제

        BookLoanResponseDto updatedLoan = bookLoanService.markAsReturned(loanId);
        return ResponseEntity.ok(updatedLoan);
    }

    // 대출 기록 삭제
    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> deleteLoan(
            @PathVariable Long loanId) { // @AuthenticationPrincipal 삭제

        bookLoanService.deleteLoan(loanId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{loanId}")
    public ResponseEntity<BookLoanResponseDto> updateLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody BookLoanUpdateRequestDto requestDto) {

        BookLoanResponseDto updatedLoan = bookLoanService.updateLoan(loanId, requestDto);
        return ResponseEntity.ok(updatedLoan);
    }

}
