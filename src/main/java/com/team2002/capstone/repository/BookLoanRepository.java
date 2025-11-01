package com.team2002.capstone.repository;

import com.team2002.capstone.domain.BookLoan;
import com.team2002.capstone.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByProfile(Profile profile);
}
