package com.team2002.capstone.service;

import com.team2002.capstone.domain.BookLoan;
import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.dto.BookLoanResponseDto;
import com.team2002.capstone.dto.BookLoanSaveRequestDto;
import com.team2002.capstone.dto.BookLoanUpdateRequestDto;
import com.team2002.capstone.exception.ResourceNotFoundException; // (ResourceNotFoundException이 없다면 IllegalArgumentException으로 대체)
import com.team2002.capstone.repository.BookLoanRepository;
import com.team2002.capstone.repository.MemberRepository; // MemberRepository 추가
import com.team2002.capstone.repository.ProfileRepository;
import com.team2002.capstone.util.SecurityUtil; // SecurityUtil import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository; // Member 조회를 위해 추가

    // 대출기록 저장
    @Transactional
    public BookLoanResponseDto saveLoan(BookLoanSaveRequestDto requestDto) {

        Profile profile = getCurrentProfile();

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

    // 대출기록 조회
    public List<BookLoanResponseDto> getMyLoans() {
        Profile profile = getCurrentProfile();

        List<BookLoan> loans = bookLoanRepository.findByProfile(profile);

        return loans.stream()
                .map(BookLoanResponseDto::new)
                .collect(Collectors.toList());
    }

    // 반납 완료 상태 변경
    @Transactional
    public BookLoanResponseDto markAsReturned(Long loanId) {
        Profile profile = getCurrentProfile();
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("대출 기록을 찾을 수 없습니다. loanId=" + loanId));


        if (!Objects.equals(loan.getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 대출 기록을 수정할 권한이 없습니다.");
        }

        loan.markAsReturned();
        return new BookLoanResponseDto(loan);
    }

    // 대출기록 삭제
    @Transactional
    public void deleteLoan(Long loanId) {
        Profile profile = getCurrentProfile();
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("대출 기록을 찾을 수 없습니다. loanId=" + loanId));


        if (!Objects.equals(loan.getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 대출 기록을 삭제할 권한이 없습니다.");
        }

        bookLoanRepository.delete(loan);
    }

    @Transactional
    public BookLoanResponseDto updateLoan(long loanId, BookLoanUpdateRequestDto requestDto) {
        Profile profile = getCurrentProfile();
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() ->  new ResourceNotFoundException("대출기록이 없습니다."));
        if (!Objects.equals(loan.getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 대출 기록을 수정할 권한이 없습니다.");
        }

        loan.update(requestDto);

        return new BookLoanResponseDto(loan);
    }

    private Profile getCurrentProfile() {

        String userEmail = SecurityUtil.getCurrentUsername();


        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        return profileRepository.findByMember(member)
                .orElseThrow(() -> new ResourceNotFoundException("현재 로그인한 사용자의 프로필을 찾을 수 없습니다."));
    }
}

