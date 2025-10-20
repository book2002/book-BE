package com.team2002.capstone.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class DiscussionCommentResponseDTO {
    private Long commentId;
    private Long discussionId;
    private String authorNickname;
    private String content;
    private LocalDateTime createdAt;
    private boolean isMyComment; // 내가 작성한 댓글인지 여부 (수정/삭제 버튼 활성화용)
}
