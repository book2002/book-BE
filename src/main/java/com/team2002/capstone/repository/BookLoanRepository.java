package com.team2002.capstone.repository;

import com.team2002.capstone.domain.BookLoan;
import com.team2002.capstone.domain.Profile;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByProfile(Profile profile);

    @Query("SELECT bl FROM BookLoan bl WHERE bl.dueDate = :today AND bl.isReturned = false")
    List<BookLoan> findLoansDueToday(@Param("today") LocalDate today);
}
