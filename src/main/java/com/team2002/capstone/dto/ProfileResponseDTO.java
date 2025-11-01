package com.team2002.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class ProfileResponseDTO {
    private Long profileId;
    private String nickName;
    private String bio;
    private String profileImageUrl;

    private int followerCount;
    private int followingCount;

    private LocalDateTime createdAt;
}
