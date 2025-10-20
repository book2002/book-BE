package com.team2002.capstone.repository;

import com.team2002.capstone.domain.Discussion;
import com.team2002.capstone.domain.ReadingGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    Page<Discussion> findAllByGroupOrderByCreatedAtDesc(ReadingGroup readingGroup, Pageable pageable);
}
