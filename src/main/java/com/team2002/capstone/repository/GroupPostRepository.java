package com.team2002.capstone.repository;

import com.team2002.capstone.domain.GroupPost;
import com.team2002.capstone.domain.ReadingGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    Page<GroupPost> findAllByGroupOrderByCreatedAtDesc(ReadingGroup group, Pageable pageable);
}
