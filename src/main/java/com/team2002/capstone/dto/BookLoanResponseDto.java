package com.team2002.capstone.dto;

import com.team2002.capstone.domain.BookLoan;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookLoanResponseDto {
    private Long loanId;
    private Long profileId;
    private String bookTitle;
    private String libraryName;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private boolean isReturned;

    public BookLoanResponseDto(BookLoan loan) {
        this.loanId = loan.getLoanId();
        this.profileId = loan.getProfile().getId();
        this.bookTitle = loan.getBookTitle();
        this.libraryName = loan.getLibraryName();
        this.checkoutDate = loan.getCheckoutDate();
        this.dueDate = loan.getDueDate();
        this.isReturned = loan.isReturned();
    }
}
