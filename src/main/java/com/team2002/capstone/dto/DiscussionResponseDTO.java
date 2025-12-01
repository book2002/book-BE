package com.team2002.capstone.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DiscussionResponseDTO {
    private Long discussionId;
    private Long groupId;
    private String authorNickname;
    private String topicTitle;
    private String topicContent;
    private boolean isClosed;
    private LocalDateTime createdAt;
    private long commentCount;
    private boolean isMyDiscussion;
    private boolean canModify;
}
