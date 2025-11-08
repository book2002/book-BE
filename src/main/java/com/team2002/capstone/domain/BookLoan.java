package com.team2002.capstone.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team2002.capstone.dto.BookLoanUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class BookLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonBackReference("profile-loans")
    private Profile profile;

    @Column(nullable = false)
    private String bookTitle;

    @Column(nullable = false)
    private String libraryName;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean isReturned = false;

    public BookLoan(Profile profile, String bookTitle, String libraryName, LocalDate checkoutDate, LocalDate dueDate) {
        this.profile = profile;
        this.bookTitle = bookTitle;
        this.libraryName = libraryName;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.isReturned = false;
    }

    public void markAsReturned() {
        this.isReturned = true;
    } // 반납완료

    public void update(BookLoanUpdateRequestDto dto) {
        this.bookTitle = dto.getBookTitle();
        this.libraryName = dto.getLibraryName();
        this.checkoutDate = LocalDate.parse(dto.getCheckoutDate());

        if (dto.getDueDate() != null && !dto.getDueDate().isEmpty()) {
            this.dueDate = LocalDate.parse(dto.getDueDate());
        } else {
            this.dueDate = null;
        }
    }
}
