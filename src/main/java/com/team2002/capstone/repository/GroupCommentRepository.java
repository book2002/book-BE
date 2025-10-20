package com.team2002.capstone.repository;

import com.team2002.capstone.domain.GroupComment;
import com.team2002.capstone.domain.GroupPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupCommentRepository extends JpaRepository<GroupComment, Long> {
    Long countByPost(GroupPost post);
    List<GroupComment> findAllByPostOrderByCreatedAtDesc(GroupPost post);
}
