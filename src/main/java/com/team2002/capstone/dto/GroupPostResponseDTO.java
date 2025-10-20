package com.team2002.capstone.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class GroupPostResponseDTO {
    private Long postId;
    private Long groupId;
    private String authorNickname;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private long commentCount;
}
