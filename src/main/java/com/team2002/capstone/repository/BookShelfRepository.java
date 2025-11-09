package com.team2002.capstone.repository;

import com.team2002.capstone.domain.BookShelf;
import com.team2002.capstone.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookShelfRepository extends JpaRepository<BookShelf, Long> {
    Optional<BookShelf> findByProfile(Profile profile);
}
