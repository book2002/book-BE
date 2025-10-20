package com.team2002.capstone.repository;

import com.team2002.capstone.domain.Discussion;
import com.team2002.capstone.domain.DiscussionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {
    Long countByDiscussion(Discussion discussion);
    List<DiscussionComment> findAllByDiscussionOrderByCreatedAtDesc(Discussion discussion);
}
