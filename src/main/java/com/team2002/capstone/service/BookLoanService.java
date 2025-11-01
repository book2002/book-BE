package com.team2002.capstone.service;

import com.team2002.capstone.domain.BookLoan;
import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.dto.BookLoanResponseDto;
import com.team2002.capstone.dto.BookLoanSaveRequestDto;
import com.team2002.capstone.repository.BookLoanRepository;
import com.team2002.capstone.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects; // Objects import 추가
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public BookLoanResponseDto saveLoan(Long profileId, BookLoanSaveRequestDto requestDto) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. profileId=" + profileId));

        LocalDate checkoutDate = LocalDate.parse(requestDto.getCheckoutDate());
        LocalDate dueDate = null;
        if (requestDto.getDueDate() != null && !requestDto.getDueDate().isEmpty()) {
            dueDate = LocalDate.parse(requestDto.getDueDate());
        }

        BookLoan newLoan = new BookLoan(
                profile,
                requestDto.getBookTitle(),
                requestDto.getLibraryName(),
                checkoutDate,
                dueDate
        );

        BookLoan savedLoan = bookLoanRepository.save(newLoan);
        return new BookLoanResponseDto(savedLoan);
    }
 //조회
    public List<BookLoanResponseDto> getMyLoans(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. profileId=" + profileId));

        List<BookLoan> loans = bookLoanRepository.findByProfile(profile);

        return loans.stream()
                .map(BookLoanResponseDto::new)
                .collect(Collectors.toList());
    }
// 반납 상태 변경
    @Transactional
    public BookLoanResponseDto markAsReturned(Long profileId, Long loanId) {
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없습니다. loanId=" + loanId));

        if (!Objects.equals(loan.getProfile().getId(), profileId)) {
            throw new IllegalStateException("이 대출 기록을 수정할 권한이 없습니다.");
        }

        loan.markAsReturned();
        return new BookLoanResponseDto(loan);
    }
// 삭제
    @Transactional
    public void deleteLoan(Long profileId, Long loanId) {
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없습니다. loanId=" + loanId));

        if (!Objects.equals(loan.getProfile().getId(), profileId)) {
            throw new IllegalStateException("이 대출 기록을 삭제할 권한이 없습니다.");
        }

        bookLoanRepository.delete(loan);
    }
}

